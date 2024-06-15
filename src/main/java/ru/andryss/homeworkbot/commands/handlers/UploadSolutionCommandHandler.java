package ru.andryss.homeworkbot.commands.handlers;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;
import ru.andryss.homeworkbot.commands.utils.MessageUtils;
import ru.andryss.homeworkbot.services.SubmissionService;
import ru.andryss.homeworkbot.services.UserService;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED;
import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.KeyboardUtils.buildOneColumnKeyboard;
import static ru.andryss.homeworkbot.commands.utils.KeyboardUtils.buildOneRowKeyboard;

@SuppressWarnings("DuplicatedCode")
@Slf4j
@Component
@RequiredArgsConstructor
public class UploadSolutionCommandHandler extends StateCommandHandler<UploadSolutionCommandHandler.UserState> {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/uploadsolution", COMMAND_UPLOADSOLUTION);

    private static final int WAITING_FOR_TOPIC_NAME = 0;
    private static final int WAITING_FOR_SUBMISSION = 1;
    private static final int WAITING_FOR_CONFIRMATION = 2;

    private static final List<List<String>> YES_NO_BUTTONS = buildOneRowKeyboard(YES_ANSWER, NO_ANSWER);
    private static final List<List<String>> STOP_WORD_BUTTON = buildOneRowKeyboard(STOP_WORD);

    @Value("${user.submission.parts.limit}")
    private int partsLimit;
    @Value("${user.submission.size.limit.mb}")
    private long sizeLimitMb;

    private final AbsSenderUtils absSenderUtils;
    private final MessageUtils messageUtils;
    private final UserService userService;
    private final SubmissionService submissionService;


    @Data
    static class UserState {
        private int state;
        private Map<String, String> availableTopics;
        private String uploadedTopic;
        private String uploadedTopicName;
        private List<PdfTranslator> uploadedParts;
        private String uploadedFile;
        private String uploadedFileExtension;
        UserState(int state, Map<String, String> availableTopics) {
            this.state = state;
            this.availableTopics = availableTopics;
        }
    }

    @Override
    protected void onCommandReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();

        if (userService.getUserName(userId).isEmpty()) {
            absSenderUtils.sendMessage(update, sender, REGISTER_FIRST);
            exitForUser(userId);
            return;
        }

        Map<String, String> availableTopics = submissionService.listAvailableTopics(userId);
        if (availableTopics.isEmpty()) {
            absSenderUtils.sendMessage(update, sender, UPLOADSOLUTION_NO_AVAILABLE_TOPICS);
            exitForUser(userId);
            return;
        }

        List<String> topicNames = new ArrayList<>(availableTopics.keySet());

        absSenderUtils.sendMessage(update, sender, UPLOADSOLUTION_AVAILABLE_TOPICS_LIST, buildNumberedList(topicNames));
        absSenderUtils.sendMessageWithKeyboard(update, sender, buildOneColumnKeyboard(topicNames), UPLOADSOLUTION_ASK_FOR_TOPIC_NAME);

        putUserState(userId, new UserState(WAITING_FOR_TOPIC_NAME, availableTopics));
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        UserState userState = getUserState(userId);
        switch (userState.getState()) {
            case WAITING_FOR_TOPIC_NAME -> onGetTopicName(update, sender);
            case WAITING_FOR_SUBMISSION -> onGetSubmission(update, sender);
            case WAITING_FOR_CONFIRMATION -> onGetConfirmation(update, sender);
        }
    }

    private void onGetTopicName(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        UserState userState = getUserState(userId);
        Map<String, String> availableTopics = userState.getAvailableTopics();
        List<String> topicNames = new ArrayList<>(availableTopics.keySet());

        if (!update.getMessage().hasText()) {
            absSenderUtils.sendMessageWithKeyboard(update, sender, buildOneColumnKeyboard(topicNames), ASK_FOR_RESENDING_TOPIC);
            return;
        }

        String topic = update.getMessage().getText();

        if (!availableTopics.containsKey(topic)) {
            absSenderUtils.sendMessageWithKeyboard(update, sender, buildOneColumnKeyboard(topicNames), TOPIC_NOT_FOUND);
            return;
        }

        userState.setUploadedTopicName(topic);
        userState.setUploadedTopic(availableTopics.get(topic));

        absSenderUtils.sendMessage(update, sender, UPLOADSOLUTION_SUBMISSION_RULES);
        absSenderUtils.sendMessageWithKeyboard(update, sender, STOP_WORD_BUTTON, UPLOADSOLUTION_ASK_FOR_SUBMISSION);

        userState.setState(WAITING_FOR_SUBMISSION);
    }

    private void onGetSubmission(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        UserState userState = getUserState(userId);

        if (userState.getUploadedParts() == null) {
            userState.setUploadedParts(new ArrayList<>(partsLimit));
        }
        List<PdfTranslator> submissions = userState.getUploadedParts();

        if (update.getMessage().hasText() && update.getMessage().getText().equals(localize(update, STOP_WORD))) {
            if (submissions.size() == 0) {
                absSenderUtils.sendMessageWithKeyboard(update, sender, STOP_WORD_BUTTON, UPLOADSOLUTION_EMPTY_SUBMISSION);
                return;
            }
            handleSubmissions(update, sender);
            return;
        }

        if (update.getMessage().hasDocument()) {
            Document document = update.getMessage().getDocument();
            if (document.getFileSize() > sizeLimitMb * 1024 * 1024) {
                absSenderUtils.sendMessage(update, sender, UPLOADSOLUTION_TOO_LARGE_FILE, sizeLimitMb);
                return;
            }
            if (document.getMimeType().startsWith("image")) {
                submissions.add(new PhotoPdfTranslator(document.getFileId(), sender));
            } else if (document.getMimeType().equals("application/pdf")) {
                submissions.add(new PdfPdfTranslator(document.getFileId(), sender));
            } else if (document.getMimeType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                submissions.add(new DocxPdfTranslator(document.getFileId(), sender));
            } else {
                if (submissions.size() > 0) {
                    absSenderUtils.sendMessageWithKeyboard(update, sender, STOP_WORD_BUTTON, UPLOADSOLUTION_INCORRECT_COMBINATION);
                    return;
                }
                handleSimpleSubmission(update, sender);
                return;
            }
        } else if (update.getMessage().hasPhoto()) {
            List<PhotoSize> photoSizes = update.getMessage().getPhoto();
            PhotoSize biggestPhotoSize = photoSizes.stream()
                    .max(Comparator.comparingInt(PhotoSize::getFileSize))
                    .orElseThrow();
            submissions.add(new PhotoPdfTranslator(biggestPhotoSize.getFileId(), sender));
        } else if (update.getMessage().hasText()) {
            submissions.add(new TextPdfTranslator(update.getMessage().getText()));
        }

        if (submissions.size() < partsLimit) {
            return;
        }

        handleSubmissions(update, sender);
    }

    private void handleSubmissions(Update update, AbsSender sender) throws TelegramApiException {
        absSenderUtils.sendMessage(update, sender, UPLOADSOLUTION_LOADING_SUBMISSION);

        Long userId = update.getMessage().getFrom().getId();
        UserState userState = getUserState(userId);

        String userName = userService.getUserName(userId).orElseThrow();

        String submissionFileId;
        File tmpDir = null;
        try {
            tmpDir = Files.createTempDirectory("submission").toFile();
            File submission = new File(tmpDir.getAbsolutePath(), userName + ".pdf");

            PdfMerger merger = new PdfMerger(new PdfDocument(new PdfWriter(submission)));

            for (PdfTranslator part : userState.getUploadedParts()) {
                File tmpFile = File.createTempFile("part", ".pdf", tmpDir);
                part.translate(tmpFile);

                PdfDocument document = new PdfDocument(new PdfReader(tmpFile));
                merger.merge(document, 1, document.getNumberOfPages());
                document.close();
            }

            merger.close();

            long size = submission.length();
            if (size > sizeLimitMb * 1024 * 1024) {
                absSenderUtils.sendMessageWithKeyboard(update, sender, STOP_WORD_BUTTON, UPLOADSOLUTION_TOO_LARGE_MERGED_FILE, sizeLimitMb);
                userState.getUploadedParts().clear();
                return;
            }

            submissionFileId = absSenderUtils.sendDocument(update, sender, submission).getDocument().getFileId();
        } catch (IOException e) {
            log.error("error occurred while handling user submission", e);
            absSenderUtils.sendMessage(update, sender, UPLOADSOLUTION_ERROR_OCCURED);
            userState.getUploadedParts().clear();
            return;
        } finally {
            FileUtils.deleteQuietly(tmpDir);
        }

        userState.setUploadedFile(submissionFileId);
        userState.setUploadedFileExtension(".pdf");

        absSenderUtils.sendMessageWithKeyboard(update, sender, YES_NO_BUTTONS, UPLOADSOLUTION_ASK_FOR_CONFIRMATION, userState.getUploadedTopicName());

        userState.setState(WAITING_FOR_CONFIRMATION);
    }

    private void handleSimpleSubmission(Update update, AbsSender sender) throws TelegramApiException {
        absSenderUtils.sendMessage(update, sender, UPLOADSOLUTION_LOADING_SUBMISSION);

        Long userId = update.getMessage().getFrom().getId();
        String userName = userService.getUserName(userId).orElseThrow();

        Document document = update.getMessage().getDocument();
        String uploadedFilename = document.getFileName();
        String extension = uploadedFilename.substring(uploadedFilename.lastIndexOf('.'));

        String submissionFileId;
        File tmpDir = null;
        try {
            tmpDir = Files.createTempDirectory("submission").toFile();

            File submission = new File(tmpDir.getAbsolutePath(), userName + extension);

            absSenderUtils.downloadFile(sender, document.getFileId(), submission);

            submissionFileId = absSenderUtils.sendDocument(update, sender, submission).getDocument().getFileId();
        } catch (IOException e) {
            log.error("error occurred while handling user submission", e);
            absSenderUtils.sendMessage(update, sender, UPLOADSOLUTION_ERROR_OCCURED);
            return;
        } finally {
            FileUtils.deleteQuietly(tmpDir);
        }

        UserState userState = getUserState(userId);
        userState.setUploadedFile(submissionFileId);
        userState.setUploadedFileExtension(extension);

        absSenderUtils.sendMessageWithKeyboard(update, sender, YES_NO_BUTTONS, UPLOADSOLUTION_ASK_FOR_CONFIRMATION, userState.getUploadedTopicName());

        userState.setState(WAITING_FOR_CONFIRMATION);
    }

    private void onGetConfirmation(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String confirmation = update.getMessage().getText();

        boolean isYes = Objects.equals(confirmation, localize(update, YES_ANSWER));
        boolean isNo = Objects.equals(confirmation, localize(update, NO_ANSWER));

        if (!update.getMessage().hasText() || !isYes && !isNo) {
            absSenderUtils.sendMessageWithKeyboard(update, sender, YES_NO_BUTTONS, ASK_FOR_RESENDING_CONFIRMATION);
            return;
        }

        if (isNo) {
            absSenderUtils.sendMessage(update, sender, UPLOADSOLUTION_CONFIRMATION_FAILURE);
            exitForUser(userId);
            return;
        }

        UserState userState = getUserState(userId);
        submissionService.uploadSubmission(userId, userState.getUploadedTopic(), userState.getUploadedFile(), userState.getUploadedFileExtension());
        absSenderUtils.sendMessage(update, sender, UPLOADSOLUTION_CONFIRMATION_SUCCESS);
        exitForUser(userId);
    }

    private String localize(Update update, String pattern) {
        String lang = update.getMessage().getFrom().getLanguageCode();
        return messageUtils.localize(lang, pattern);
    }

    /**
     * Interface to work with uploaded user solution
     */
    private interface PdfTranslator {
        /**
         * Translates solution into pdf file
         * @param destination pdf file to write
         */
        void translate(File destination) throws IOException, TelegramApiException;
    }

    private record TextPdfTranslator(String text) implements PdfTranslator {
        @Override
        public void translate(File destination) throws IOException {
            try (FileOutputStream fos = new FileOutputStream(destination)) {
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fos));
                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDocument);

                Paragraph p = new Paragraph();
                p.setFont(PdfFontFactory.createFont("fonts/ArialRegular.ttf", PREFER_EMBEDDED));
                p.add(text);

                document.add(p);

                document.close();
            }
        }
    }

    @RequiredArgsConstructor
    private class PhotoPdfTranslator implements PdfTranslator {

        private final String photoId;
        private final AbsSender sender;

        @Override
        public void translate(File destination) throws TelegramApiException, IOException {
            File photo = null;
            try {
                photo = File.createTempFile("photo", "");
                absSenderUtils.downloadFile(sender, photoId, photo);

                ImageData imageData = ImageDataFactory.create(photo.getAbsolutePath());

                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));
                pdfDocument.setDefaultPageSize(new PageSize(imageData.getWidth(), imageData.getHeight()));

                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDocument);
                document.setMargins(0, 0, 0, 0);

                Image image = new Image(imageData);
                document.add(image);

                document.close();
            } finally {
                FileUtils.deleteQuietly(photo);
            }
        }
    }

    @RequiredArgsConstructor
    private class PdfPdfTranslator implements PdfTranslator {

        private final String fileId;
        private final AbsSender sender;

        @Override
        public void translate(File destination) throws TelegramApiException {
            absSenderUtils.downloadFile(sender, fileId, destination);
        }
    }

    @RequiredArgsConstructor
    private class DocxPdfTranslator implements PdfTranslator {

        private final String fileId;
        private final AbsSender sender;

        @Override
        public void translate(File destination) throws TelegramApiException, IOException {
            File docx = null;
            try {
                docx = File.createTempFile("docx", ".docx");
                absSenderUtils.downloadFile(sender, fileId, docx);

                XWPFDocument document = new XWPFDocument(new FileInputStream(docx));
                PdfOptions options = PdfOptions.create();
                OutputStream out = new FileOutputStream(destination);
                PdfConverter.getInstance().convert(document, out, options);
            } finally {
                FileUtils.deleteQuietly(docx);
            }
        }
    }
}
