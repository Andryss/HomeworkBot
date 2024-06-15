package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.handlers.Util.createEmptyUpdate;
import static ru.andryss.homeworkbot.commands.handlers.Util.createTextUpdate;

@SpringBootTest
class UploadSolutionCommandHandlerTest extends CommandHandlerBaseTest {

    @Autowired
    UploadSolutionCommandHandler commandHandler;

    @BeforeEach
    void clear() {
        clearDatabase();
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_notRegistered_sendRegisterFirst() {
        long chatId = 10090L;
        long userId = 100090L;

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_noTopicsCreated_sendNoTopics() {
        long chatId = 10091L;
        long userId = 100091L;

        register(chatId, userId, "receiveUploadSolution noTopicsCreated sendNoTopics");

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(UPLOADSOLUTION_NO_AVAILABLE_TOPICS);
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_someTopicsCreated_sendTopics() {
        long chatId = 10092L;
        long userId = 100092L;

        register(chatId, userId, "receiveUploadSolution someTopicsCreated sendTopics");

        createTopic(chatId, userId, "First topic");
        createTopic(chatId, userId, "Second topic");

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(UPLOADSOLUTION_AVAILABLE_TOPICS_LIST, "1) First topic\n2) Second topic\n");
        verifySendKeyboard(columnKeyboard("First topic", "Second topic"), UPLOADSOLUTION_ASK_FOR_TOPIC_NAME);
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_nonExistingTopic_sendTopicNotFound() {
        long chatId = 10093L;
        long userId = 100093L;

        register(chatId, userId, "receiveUploadSolution nonExistingTopic sendTopicNotFound");
        createTopic(chatId, userId, "First topic");

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(UPLOADSOLUTION_AVAILABLE_TOPICS_LIST, "1) First topic\n");
        verifySendKeyboard(columnKeyboard("First topic"), UPLOADSOLUTION_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "Non existing topic"));
        verifySendKeyboard(columnKeyboard("First topic"), TOPIC_NOT_FOUND);
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_emptyTopic_sendWarningMessage() {
        long chatId = 10094L;
        long userId = 100094L;

        register(chatId, userId, "receiveUploadSolution emptyTopic sendWarningMessage");
        createTopic(chatId, userId, "First topic");

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(UPLOADSOLUTION_AVAILABLE_TOPICS_LIST, "1) First topic\n");
        verifySendKeyboard(columnKeyboard("First topic"), UPLOADSOLUTION_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendKeyboard(columnKeyboard("First topic"), ASK_FOR_RESENDING_TOPIC);
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_availableTopic_sendWaitSubmissionMessage() {
        long chatId = 10095L;
        long userId = 100095L;

        register(chatId, userId, "receiveUploadSolution availableTopic sendWaitSubmissionMessage");
        createTopic(chatId, userId, "First topic");

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(UPLOADSOLUTION_AVAILABLE_TOPICS_LIST, "1) First topic\n");
        verifySendKeyboard(columnKeyboard("First topic"), UPLOADSOLUTION_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "First topic"));
        verifySendMessage(UPLOADSOLUTION_SUBMISSION_RULES);
        verifySendKeyboard(columnKeyboard(STOP_WORD), UPLOADSOLUTION_ASK_FOR_SUBMISSION);
    }
}