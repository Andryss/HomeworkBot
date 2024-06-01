package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.handlers.Util.*;

@SpringBootTest
class DumpTopicCommandHandlerTest extends CommandHandlerBaseTest {
    
    @Autowired
    DumpTopicCommandHandler commandHandler;

    @BeforeEach
    void clear() {
        clearDatabase();
    }

    @Test
    @SneakyThrows
    void receiveDumpTopic_notRegistered_sendRegisterFirst() {
        long chatId = 10070L;
        long userId = 100070L;
        String chatIdStr = Long.toString(chatId);

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveDumpTopic_notLeader_sendNoLeader() {
        long chatId = 10071L;
        long userId = 100071L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveDumpTopic notLeader sendNoLeader");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "not leader"));
        verifySendMessage(chatIdStr, NOT_LEADER);
    }

    @Test
    @SneakyThrows
    void receiveDumpTopic_noTopicsCreated_sendNoTopics() {
        long chatId = 10072L;
        long userId = 100072L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveDumpTopic noTopicsCreated sendNoTopics");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, NO_TOPICS);
    }

    @Test
    @SneakyThrows
    void receiveDumpTopic_someTopicsCreated_sendTopics() {
        long chatId = 10073L;
        long userId = 100073L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveDumpTopic someTopicsCreated sendTopics");
        createTopic(chatId, userId, "First topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, DUMPTOPIC_ASK_FOR_TOPIC_NAME);
    }

    @Test
    @SneakyThrows
    void receiveDumpTopic_nonExistingTopic_sendTopicNotFound() {
        long chatId = 10073L;
        long userId = 100073L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveDumpTopic nonExistingTopic sendTopicNotFound");
        createTopic(chatId, userId, "First topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, DUMPTOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "Non existing topic"));
        verifySendMessage(chatIdStr, TOPIC_NOT_FOUND);
    }

    @Test
    @SneakyThrows
    void receiveDumpTopic_emptyTopic_sendWarningMessage() {
        long chatId = 10074L;
        long userId = 100074L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveDumpTopic emptyTopic sendWarningMessage");
        createTopic(chatId, userId, "First topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, DUMPTOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, ASK_FOR_RESENDING_TOPIC);
    }

    @Test
    @SneakyThrows
    void receiveDumpTopic_noSubmissions_sendNoSubmissionsMessage() {
        long chatId = 10075L;
        long userId = 100075L;
        String chatIdStr = Long.toString(chatId);
        String topic = "First topic";

        register(chatId, userId, "receiveDumpTopic noSubmissions sendNoSubmissionsMessage");
        createTopic(chatId, userId, topic);

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, DUMPTOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, DUMPTOPIC_NO_SUBMISSIONS);
    }
}