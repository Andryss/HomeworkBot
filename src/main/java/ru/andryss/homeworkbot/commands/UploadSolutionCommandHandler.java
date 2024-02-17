package ru.andryss.homeworkbot.commands;

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
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.SubmissionService;
import ru.andryss.homeworkbot.services.UserService;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED;
import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadSolutionCommandHandler extends StateCommandHandler<UploadSolutionCommandHandler.UserState> {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/uploadsolution", "загрузить решение домашнего задания");

    private static final int WAITING_FOR_TOPIC_NAME = 0;
    private static final int WAITING_FOR_SUBMISSION = 1;
    private static final int WAITING_FOR_CONFIRMATION = 2;

    private static final int USER_SUBMISSIONS_LIMIT = 10;
    private static final int SIZE_5MB = 5 * 1024 * 1024;

    private static final List<List<String>> YES_NO_BUTTONS = List.of(List.of(YES_ANSWER, NO_ANSWER));
    private static final List<List<String>> STOP_WORD_BUTTON = List.of(List.of(STOP_WORD));

    private final UserService userService;
    private final SubmissionService submissionService;


    @Data
    static class UserState {
        private int state;
        private List<String> availableTopics;
        private String uploadedTopic;
        private List<PdfTranslator> uploadedParts;
        private String uploadedFile;
        private String uploadedFileExtension;
        UserState(int state, List<String> availableTopics) {
            this.state = state;
            this.availableTopics = availableTopics;
        }
    }

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

        StringBuilder builder = new StringBuilder();
        List<List<String>> topicsKeyboard = new ArrayList<>(availableTopics.size());
        for (String topic : availableTopics) {
            builder.append('\n').append("• ").append(topic);
            topicsKeyboard.add(List.of(topic));
        }

        sendMessage(update, sender, String.format(UPLOADSOLUTION_AVAILABLE_TOPICS_LIST, builder));
        sendMessageWithKeyboard(update, sender, UPLOADSOLUTION_ASK_FOR_TOPIC_NAME, topicsKeyboard);

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
        List<String> availableTopics = userState.getAvailableTopics();

        if (!update.getMessage().hasText()) {
            sendMessageWithKeyboard(update, sender, ASK_FOR_RESENDING_TOPIC, availableTopics.stream().map(List::of).toList());
            return;
        }

        String topic = update.getMessage().getText();

        if (!availableTopics.contains(topic)) {
            sendMessageWithKeyboard(update, sender, TOPIC_NOT_FOUND, availableTopics.stream().map(List::of).toList());
            return;
        }

        userState.setUploadedTopic(topic);

        sendMessage(update, sender, UPLOADSOLUTION_SUBMISSION_RULES);
        sendMessageWithKeyboard(update, sender, UPLOADSOLUTION_ASK_FOR_SUBMISSION, STOP_WORD_BUTTON);

        userState.setState(WAITING_FOR_SUBMISSION);
    }

    private void onGetSubmission(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        UserState userState = getUserState(userId);

        if (userState.getUploadedParts() == null) {
            userState.setUploadedParts(new ArrayList<>(USER_SUBMISSIONS_LIMIT));
        }
        List<PdfTranslator> submissions = userState.getUploadedParts();

        if (update.getMessage().hasText() && update.getMessage().getText().equals(STOP_WORD)) {
            if (submissions.size() == 0) {
                sendMessageWithKeyboard(update, sender, UPLOADSOLUTION_EMPTY_SUBMISSION, STOP_WORD_BUTTON);
                return;
            }
            handleSubmissions(update, sender);
            return;
        }

        if (update.getMessage().hasDocument()) {
            Document document = update.getMessage().getDocument();
            if (document.getFileSize() > SIZE_5MB) {
                sendMessage(update, sender, UPLOADSOLUTION_TOO_LARGE_FILE);
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
                    sendMessageWithKeyboard(update, sender, UPLOADSOLUTION_INCORRECT_COMBINATION, STOP_WORD_BUTTON);
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

        if (submissions.size() < USER_SUBMISSIONS_LIMIT) {
            return;
        }

        handleSubmissions(update, sender);
    }

    private void handleSubmissions(Update update, AbsSender sender) throws TelegramApiException {
        sendMessage(update, sender, UPLOADSOLUTION_LOADING_SUBMISSION);

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
            if (size > SIZE_5MB) {
                sendMessageWithKeyboard(update, sender, UPLOADSOLUTION_TOO_LARGE_MERGED_FILE, STOP_WORD_BUTTON);
                userState.getUploadedParts().clear();
                return;
            }

            submissionFileId = sendDocument(update, sender, submission).getDocument().getFileId();
        } catch (IOException e) {
            log.error("error occurred while handling user submission", e);
            sendMessage(update, sender, UPLOADSOLUTION_ERROR_OCCURED);
            userState.getUploadedParts().clear();
            return;
        } finally {
            FileUtils.deleteQuietly(tmpDir);
        }

        userState.setUploadedFile(submissionFileId);
        userState.setUploadedFileExtension(".pdf");

        sendMessageWithKeyboard(update, sender, String.format(UPLOADSOLUTION_ASK_FOR_CONFIRMATION, userState.getUploadedTopic()), YES_NO_BUTTONS);

        userState.setState(WAITING_FOR_CONFIRMATION);
    }

    private void handleSimpleSubmission(Update update, AbsSender sender) throws TelegramApiException {
        sendMessage(update, sender, UPLOADSOLUTION_LOADING_SUBMISSION);

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

            downloadFile(sender, document.getFileId(), submission);

            submissionFileId = sendDocument(update, sender, submission).getDocument().getFileId();
        } catch (IOException e) {
            log.error("error occurred while handling user submission", e);
            sendMessage(update, sender, UPLOADSOLUTION_ERROR_OCCURED);
            return;
        } finally {
            FileUtils.deleteQuietly(tmpDir);
        }

        UserState userState = getUserState(userId);
        userState.setUploadedFile(submissionFileId);
        userState.setUploadedFileExtension(extension);

        sendMessageWithKeyboard(update, sender, String.format(UPLOADSOLUTION_ASK_FOR_CONFIRMATION, userState.getUploadedTopic()), YES_NO_BUTTONS);

        userState.setState(WAITING_FOR_CONFIRMATION);
    }

    private void onGetConfirmation(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String confirmation = update.getMessage().getText();

        if (!update.getMessage().hasText() || !confirmation.equals(YES_ANSWER) && !confirmation.equals(NO_ANSWER)) {
            sendMessageWithKeyboard(update, sender, ASK_FOR_RESENDING_CONFIRMATION, YES_NO_BUTTONS);
            return;
        }


        if (confirmation.equals(NO_ANSWER)) {
            sendMessage(update, sender, UPLOADSOLUTION_CONFIRMATION_FAILURE);
            clearUserState(userId);
            exitForUser(userId);
            return;
        }

        UserState userState = getUserState(userId);
        submissionService.uploadSubmission(userId, userState.getUploadedTopic(), userState.getUploadedFile(), userState.getUploadedFileExtension());
        sendMessage(update, sender, UPLOADSOLUTION_CONFIRMATION_SUCCESS);
        clearUserState(userId);
        exitForUser(userId);
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

    private record PhotoPdfTranslator(String photoId, AbsSender sender) implements PdfTranslator {
        @Override
        public void translate(File destination) throws TelegramApiException, IOException {
            File photo = null;
            try {
                photo = File.createTempFile("photo", "");
                downloadFile(sender, photoId, photo);

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

    private record PdfPdfTranslator(String fileId, AbsSender sender) implements PdfTranslator {
        @Override
        public void translate(File destination) throws TelegramApiException {
            downloadFile(sender, fileId, destination);
        }
    }

    private record DocxPdfTranslator(String fileId, AbsSender sender) implements PdfTranslator {
        @Override
        public void translate(File destination) throws TelegramApiException, IOException {
            File docx = null;
            try {
                docx = File.createTempFile("docx", ".docx");
                downloadFile(sender, fileId, docx);

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
