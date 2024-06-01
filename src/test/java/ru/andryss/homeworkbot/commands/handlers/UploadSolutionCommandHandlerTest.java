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
        String chatIdStr = Long.toString(chatId);

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_noTopicsCreated_sendNoTopics() {
        long chatId = 10091L;
        long userId = 100091L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveUploadSolution noTopicsCreated sendNoTopics");

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, UPLOADSOLUTION_NO_AVAILABLE_TOPICS);
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_someTopicsCreated_sendTopics() {
        long chatId = 10092L;
        long userId = 100092L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveUploadSolution someTopicsCreated sendTopics");

        createTopic(chatId, userId, "First topic");
        createTopic(chatId, userId, "Second topic");

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, UPLOADSOLUTION_ASK_FOR_TOPIC_NAME);
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_nonExistingTopic_sendTopicNotFound() {
        long chatId = 10093L;
        long userId = 100093L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveUploadSolution nonExistingTopic sendTopicNotFound");
        createTopic(chatId, userId, "First topic");

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, UPLOADSOLUTION_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "Non existing topic"));
        verifySendMessage(chatIdStr, TOPIC_NOT_FOUND);
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_emptyTopic_sendWarningMessage() {
        long chatId = 10094L;
        long userId = 100094L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveUploadSolution emptyTopic sendWarningMessage");
        createTopic(chatId, userId, "First topic");

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, UPLOADSOLUTION_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, ASK_FOR_RESENDING_TOPIC);
    }

    @Test
    @SneakyThrows
    void receiveUploadSolution_availableTopic_sendWaitSubmissionMessage() {
        long chatId = 10095L;
        long userId = 100095L;
        String chatIdStr = Long.toString(chatId);
        String topic = "First topic";

        register(chatId, userId, "receiveUploadSolution availableTopic sendWaitSubmissionMessage");
        createTopic(chatId, userId, topic);

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, UPLOADSOLUTION_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, UPLOADSOLUTION_ASK_FOR_SUBMISSION);
    }
}