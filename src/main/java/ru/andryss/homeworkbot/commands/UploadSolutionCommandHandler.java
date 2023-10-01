package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.SubmissionService;
import ru.andryss.homeworkbot.services.UserService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.*;

@Component
@RequiredArgsConstructor
public class UploadSolutionCommandHandler implements CommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/uploadsolution", "загрузить решение домашнего задания");

    private static final String REGISTER_FIRST = "Для начала зарегистрируйтесь\n/start";
    private static final String NO_AVAILABLE_TOPICS = "Нет доступных домашних заданий для сдачи";
    private static final String AVAILABLE_TOPICS_LIST = "Список доступных домашних заданий для сдачи: %s";
    private static final String ASK_FOR_TOPIC_NAME = "Пожалуйста, введите название домашнего задания, которое хотите сдать:";
    private static final String ASK_FOR_RESENDING_TOPIC = "Пожалуйста, введите название домашнего задания:";
    private static final String ASK_FOR_SUBMISSION = "Пожалуйста, загрузите ваше решение в виде документа:";
    private static final String ASK_FOR_RESENDING_SUBMISSION = "Пожалуйста, загрузите ваше решение в виде документа:";
    private static final String ASK_FOR_CONFIRMATION = "Вы уверены, что хотите загрузить такое решение, как ответ на \"%s\"? (да/нет)";
    private static final String YES_ANSWER = "да";
    private static final String NO_ANSWER = "нет";
    private static final String ASK_FOR_RESENDING_CONFIRMATION = "Пожалуйста, выберите \"да\" или \"нет\":";
    private static final String CONFIRMATION_SUCCESS = "Ваше решение успешно загружено\n/help";
    private static final String CONFIRMATION_FAILURE = "Не удалось загрузить решение\n/help";

    private static final int WAITING_FOR_TOPIC_NAME = 0;
    private static final int WAITING_FOR_SUBMISSION = 1;
    private static final int WAITING_FOR_CONFIRMATION = 2;

    private static final List<List<String>> YES_NO_BUTTONS = List.of(List.of(YES_ANSWER, NO_ANSWER));

    private final Map<Long, Integer> userToState = new ConcurrentHashMap<>();
    private final Map<Long, Runnable> userToOnExitHandler = new ConcurrentHashMap<>();
    private final Map<Long, List<String>> userToAvailableTopics = new ConcurrentHashMap<>();
    private final Map<Long, String> userToUploadedTopic = new ConcurrentHashMap<>();
    private final Map<Long, String> userToUploadedFile = new ConcurrentHashMap<>();
    private final Map<Long, String> userToUploadedFileExtension = new ConcurrentHashMap<>();

    private final UserService userService;
    private final SubmissionService submissionService;


    @Override
    public void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();

        if (userService.getUserName(userId) == null) {
            sendMessage(update, sender, REGISTER_FIRST);
            onExitHandler.run();
            return;
        }

        List<String> availableTopics = submissionService.listAvailableTopics(userId);
        if (availableTopics.isEmpty()) {
            sendMessage(update, sender, NO_AVAILABLE_TOPICS);
            onExitHandler.run();
            return;
        }
        userToAvailableTopics.put(userId, availableTopics);

        onGetCommandAndAvailableTopics(update, sender);
        userToState.put(userId, WAITING_FOR_TOPIC_NAME);
        userToOnExitHandler.put(userId, onExitHandler);
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
        sendMessage(update, sender, String.format(AVAILABLE_TOPICS_LIST, builder));

        sendMessageWithKeyboard(update, sender, ASK_FOR_TOPIC_NAME, List.of(availableTopics));
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
            sendMessageWithKeyboard(update, sender, ASK_FOR_RESENDING_TOPIC, List.of(availableTopics));
            userToState.put(userId, WAITING_FOR_TOPIC_NAME);
            return;
        }

        userToUploadedTopic.put(userId, topic);

        sendMessage(update, sender, ASK_FOR_SUBMISSION);
        userToState.put(userId, WAITING_FOR_SUBMISSION);
    }

    private void onGetSubmission(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();

        if (!update.getMessage().hasDocument()) {
            sendMessage(update, sender, ASK_FOR_RESENDING_SUBMISSION);
            userToState.put(userId, WAITING_FOR_SUBMISSION);
            return;
        }

        Document document = update.getMessage().getDocument();
        String fileId = document.getFileId();
        userToUploadedFile.put(userId, fileId);

        String fileName = document.getFileName();
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        userToUploadedFileExtension.put(userId, extension);

        String userName = userService.getUserName(userId);
        sendDocument(update, sender, fileId, userName + extension);

        sendMessageWithKeyboard(update, sender, String.format(ASK_FOR_CONFIRMATION, userToUploadedTopic.get(userId)), YES_NO_BUTTONS);
        userToState.put(userId, WAITING_FOR_CONFIRMATION);
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
            sendMessage(update, sender, CONFIRMATION_FAILURE);
            userToState.remove(userId);
            userToAvailableTopics.remove(userId);
            userToUploadedTopic.remove(userId);
            userToUploadedFile.remove(userId);
            userToUploadedFileExtension.remove(userId);
            userToOnExitHandler.remove(userId).run();
            return;
        }

        submissionService.uploadSubmission(userId, userToUploadedTopic.get(userId), userToUploadedFile.get(userId), userToUploadedFileExtension.get(userId));
        sendMessage(update, sender, CONFIRMATION_SUCCESS);
        userToState.remove(userId);
        userToAvailableTopics.remove(userId);
        userToUploadedTopic.remove(userId);
        userToUploadedFile.remove(userId);
        userToUploadedFileExtension.remove(userId);
        userToOnExitHandler.remove(userId).run();
    }
}
