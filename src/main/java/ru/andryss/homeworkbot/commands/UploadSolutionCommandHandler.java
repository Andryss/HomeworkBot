package ru.andryss.homeworkbot.commands;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.SubmissionService;
import ru.andryss.homeworkbot.services.UserService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadSolutionCommandHandler extends AbstractCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/uploadsolution", "загрузить решение домашнего задания");

    private static final int WAITING_FOR_TOPIC_NAME = 0;
    private static final int WAITING_FOR_SUBMISSION = 1;
    private static final int WAITING_FOR_CONFIRMATION = 2;

    private static final List<List<String>> YES_NO_BUTTONS = List.of(List.of(YES_ANSWER, NO_ANSWER));

    private final Map<Long, Integer> userToState = new ConcurrentHashMap<>();
    private final Map<Long, List<String>> userToAvailableTopics = new ConcurrentHashMap<>();
    private final Map<Long, String> userToUploadedTopic = new ConcurrentHashMap<>();
    private final Map<Long, String> userToUploadedFile = new ConcurrentHashMap<>();
    private final Map<Long, UploadPhotoInfo> userToUploadPhotoInfo = new ConcurrentHashMap<>();
    private final Map<Long, String> userToUploadedFileExtension = new ConcurrentHashMap<>();

    private final UserService userService;
    private final SubmissionService submissionService;


    @Override
    protected void onCommandReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();

        if (userService.getUserName(userId).isEmpty()) {
            sendMessage(update, sender, REGISTER_FIRST);
            exitForUser(userId);
            return;
        }

        List<String> availableTopics = submissionService.listAvailableTopics(userId);
        if (availableTopics.isEmpty()) {
            sendMessage(update, sender, UPLOADSOLUTION_NO_AVAILABLE_TOPICS);
            exitForUser(userId);
            return;
        }
        userToAvailableTopics.put(userId, availableTopics);

        onGetCommandAndAvailableTopics(update, sender);
        userToState.put(userId, WAITING_FOR_TOPIC_NAME);
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        Integer userState = userToState.get(userId);
        switch (userState) {
            case WAITING_FOR_TOPIC_NAME -> onGetTopicName(update, sender);
            case WAITING_FOR_SUBMISSION -> onGetSubmission(update, sender);
            case WAITING_FOR_CONFIRMATION -> onGetConfirmation(update, sender);
        }
    }

    private void onGetCommandAndAvailableTopics(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        List<String> availableTopics = userToAvailableTopics.get(userId);

        StringBuilder builder = new StringBuilder();
        for (String topic : availableTopics) {
            builder.append('\n').append("• ").append(topic);
        }
        sendMessage(update, sender, String.format(UPLOADSOLUTION_AVAILABLE_TOPICS_LIST, builder));

        sendMessageWithKeyboard(update, sender, UPLOADSOLUTION_ASK_FOR_TOPIC_NAME, List.of(availableTopics));
        userToState.put(userId, WAITING_FOR_TOPIC_NAME);
    }

    private void onGetTopicName(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();

        if (!update.getMessage().hasText() || !userToAvailableTopics.get(userId).contains(update.getMessage().getText())) {
            List<String> topics = userToAvailableTopics.get(userId);
            sendMessageWithKeyboard(update, sender, ASK_FOR_RESENDING_TOPIC, List.of(topics));
            userToState.put(userId, WAITING_FOR_TOPIC_NAME);
            return;
        }

        String topic = update.getMessage().getText();
        List<String> availableTopics = userToAvailableTopics.get(userId);

        if (!availableTopics.contains(topic)) {
            List<List<String>> availableTopicsKeyboard = availableTopics.stream().map(List::of).toList();
            sendMessageWithKeyboard(update, sender, ASK_FOR_RESENDING_TOPIC, availableTopicsKeyboard);
            userToState.put(userId, WAITING_FOR_TOPIC_NAME);
            return;
        }

        userToUploadedTopic.put(userId, topic);

        sendMessage(update, sender, UPLOADSOLUTION_SUBMISSION_RULES);
        sendMessage(update, sender, UPLOADSOLUTION_ASK_FOR_SUBMISSION);
        userToState.put(userId, WAITING_FOR_SUBMISSION);
    }

    private void onGetSubmission(Update update, AbsSender sender) throws TelegramApiException {
        if (update.getMessage().hasPhoto() && update.getMessage().getMediaGroupId() != null) {
            collectPhoto(update, sender);
            return;
        }
        sendMessage(update, sender, UPLOADSOLUTION_LOADING_SUBMISSION);

        Long userId = update.getMessage().getFrom().getId();
        File tmpDir = null;
        String fileName;
        Message message;

        try {
            tmpDir = Files.createTempDirectory("submission").toFile();
            String tmpDirPrefix = tmpDir.getAbsolutePath() + '/';

            File submission;
            if (update.getMessage().hasDocument()) {
                submission = extractDocument(tmpDirPrefix, update, sender);
            } else if (update.getMessage().hasPhoto()) {
                submission = extractPhoto(tmpDirPrefix, update, sender);
            } else if (update.getMessage().hasText()) {
                submission = extractText(tmpDirPrefix, update);
            } else {
                submission = null;
            }

            if (submission == null) {
                sendMessage(update, sender, UPLOADSOLUTION_ASK_FOR_RESENDING_SUBMISSION);
                userToState.put(userId, WAITING_FOR_SUBMISSION);
                return;
            }

            fileName = submission.getName();
            message = sendDocument(update, sender, submission);
        } catch (IOException e) {
            log.error("onGetSubmission error occurred", e);
            sendMessage(update, sender, UPLOADSOLUTION_ERROR_OCCURED);
            userToState.put(userId, WAITING_FOR_SUBMISSION);
            return;
        } finally {
            FileUtils.deleteQuietly(tmpDir);
        }

        Document document = message.getDocument();
        String fileId = document.getFileId();
        userToUploadedFile.put(userId, fileId);

        String extension = fileName.substring(fileName.lastIndexOf('.'));
        userToUploadedFileExtension.put(userId, extension);

        sendMessageWithKeyboard(update, sender, String.format(UPLOADSOLUTION_ASK_FOR_CONFIRMATION, userToUploadedTopic.get(userId)), YES_NO_BUTTONS);
        userToState.put(userId, WAITING_FOR_CONFIRMATION);
    }

    private void collectPhoto(Update update, AbsSender sender) throws TelegramApiException {
        Long id = update.getMessage().getFrom().getId();

        if (!userToUploadPhotoInfo.containsKey(id)) {
            sendMessage(update, sender, UPLOADSOLUTION_LOADING_SUBMISSION);
        }

        UploadPhotoInfo uploadInfo = userToUploadPhotoInfo.computeIfAbsent(id, l -> new UploadPhotoInfo(l, new ArrayList<>(), null));
        Thread callback = uploadInfo.getCallback();
        if (callback != null) {
            callback.interrupt();
            try {
                callback.join();
            } catch (InterruptedException e) {
                return;
            }
        }

        uploadInfo.getPhotos().add(update.getMessage().getPhoto().stream()
                .max(Comparator.comparingInt(PhotoSize::getFileSize))
                .orElseThrow()
        );

        uploadInfo.setCallback(new Thread(() -> {
            try {
                Thread.sleep(1_000);
                handlePhotoGroup(update, sender, id);
            } catch (InterruptedException e) {
                // ignore
            }
        }));

        uploadInfo.getCallback().start();
    }

    private void handlePhotoGroup(Update update, AbsSender sender, Long userId) {
        UploadPhotoInfo uploadInfo = userToUploadPhotoInfo.get(userId);
        List<PhotoSize> photoSizes = uploadInfo.getPhotos();

        String userName = userService.getUserName(userId).orElseThrow();

        File tmpDir = null;
        try {
            tmpDir = Files.createTempDirectory("submission").toFile();

            String fileName = tmpDir.getAbsolutePath() + '/' + userName + ".pdf";

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                com.itextpdf.text.Document document = new com.itextpdf.text.Document(new Rectangle(0, 0), 0, 0, 0, 0);
                PdfWriter writer = PdfWriter.getInstance(document, fos);

                writer.open();
                document.open();

                for (PhotoSize photoSize : photoSizes) {
                    File image = new File(tmpDir.getAbsolutePath() + photoSize.getFileUniqueId());
                    if (!image.createNewFile()) {
                        throw new IOException("can't create file " + image.getAbsolutePath());
                    }

                    downloadFile(sender, photoSize.getFileId(), image);

                    document.setPageSize(new Rectangle(photoSize.getWidth(), photoSize.getHeight()));
                    document.newPage();
                    document.add(Image.getInstance(image.getAbsolutePath()));
                }

                document.close();
                writer.close();
            }

            Message message = sendDocument(update, sender, new File(fileName));

            userToUploadPhotoInfo.remove(userId);

            userToUploadedFile.put(userId, message.getDocument().getFileId());

            userToUploadedFileExtension.put(userId, ".pdf");

            sendMessageWithKeyboard(update, sender, String.format(UPLOADSOLUTION_ASK_FOR_CONFIRMATION, userToUploadedTopic.get(userId)), YES_NO_BUTTONS);
            userToState.put(userId, WAITING_FOR_CONFIRMATION);
        } catch (Exception e) {
            log.error("exception occurred during photo group handling", e);
        } finally {
            FileUtils.deleteQuietly(tmpDir);
        }
    }

    private File extractDocument(String tmpDirPrefix, Update update, AbsSender sender) throws TelegramApiException, IOException {
        Long id = update.getMessage().getFrom().getId();
        String userName = userService.getUserName(id).orElseThrow();

        Document document = update.getMessage().getDocument();
        Long fileSize = document.getFileSize();
        if (fileSize > 5 * 1024 * 1024) {
            sendMessage(update, sender, UPLOADSOLUTION_TOO_LARGE_FILE);
            return null;
        }

        String documentFileName = document.getFileName();
        String extension = documentFileName.substring(documentFileName.lastIndexOf('.'));

        String fileName = tmpDirPrefix + userName + extension;
        File submission = new File(fileName);
        if (!submission.createNewFile()) {
            throw new IOException("can't create file " + submission.getAbsolutePath());
        }

        String fileId = document.getFileId();
        downloadFile(sender, fileId, submission);

        return submission;
    }

    private File extractPhoto(String tmpDirPrefix, Update update, AbsSender sender) throws TelegramApiException, IOException {
        Long id = update.getMessage().getFrom().getId();

        List<PhotoSize> photoSizes = update.getMessage().getPhoto();
        PhotoSize biggestPhotoSize = photoSizes.stream()
                .max(Comparator.comparingInt(PhotoSize::getFileSize))
                .orElseThrow();

        String userName = userService.getUserName(id).orElseThrow();
        String fileName = tmpDirPrefix + userName + ".pdf";
        File submission = new File(fileName);
        if (!submission.createNewFile()) {
            throw new IOException("can't create file " + submission.getAbsolutePath());
        }

        downloadFile(sender, biggestPhotoSize.getFileId(), submission);

        return submission;
    }

    private File extractText(String tmpDirPrefix, Update update) throws IOException {
        Long id = update.getMessage().getFrom().getId();
        String userName = userService.getUserName(id).orElseThrow();
        String fileName = tmpDirPrefix + userName + ".txt";
        File submission = new File(fileName);
        if (!submission.createNewFile()) {
            throw new IOException("can't create file " + submission.getAbsolutePath());
        }

        String text = update.getMessage().getText();

        try (FileWriter writer = new FileWriter(submission)) {
            writer.write(text);
        }

        return submission;
    }

    private void onGetConfirmation(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String confirmation = update.getMessage().getText();

        if (!update.getMessage().hasText() || !confirmation.equals(YES_ANSWER) && !confirmation.equals(NO_ANSWER)) {
            sendMessageWithKeyboard(update, sender, ASK_FOR_RESENDING_CONFIRMATION, YES_NO_BUTTONS);
            userToState.put(userId, WAITING_FOR_CONFIRMATION);
            return;
        }


        if (confirmation.equals(NO_ANSWER)) {
            sendMessage(update, sender, UPLOADSOLUTION_CONFIRMATION_FAILURE);
            userToState.remove(userId);
            userToAvailableTopics.remove(userId);
            userToUploadedTopic.remove(userId);
            userToUploadedFile.remove(userId);
            userToUploadedFileExtension.remove(userId);
            exitForUser(userId);
            return;
        }

        submissionService.uploadSubmission(userId, userToUploadedTopic.get(userId), userToUploadedFile.get(userId), userToUploadedFileExtension.get(userId));
        sendMessage(update, sender, UPLOADSOLUTION_CONFIRMATION_SUCCESS);
        userToState.remove(userId);
        userToAvailableTopics.remove(userId);
        userToUploadedTopic.remove(userId);
        userToUploadedFile.remove(userId);
        userToUploadedFileExtension.remove(userId);
        exitForUser(userId);
    }

    @Data
    @AllArgsConstructor
    private static class UploadPhotoInfo {
        private long userId;
        private List<PhotoSize> photos;
        private Thread callback;
    }
}
