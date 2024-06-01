package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.handlers.Util.*;

class RemoveTopicCommandHandlerTest extends CommandHandlerBaseTest {

    @Autowired
    RemoveTopicCommandHandler commandHandler;

    @BeforeEach
    void clear() {
        clearDatabase();
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_notRegistered_sendRegisterFirst() {
        long chatId = 10050L;
        long userId = 100050L;
        String chatIdStr = Long.toString(chatId);

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_notLeader_sendNoLeader() {
        long chatId = 10051L;
        long userId = 100051L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveCreateTopic notLeader sendNoLeader");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "not leader"));
        verifySendMessage(chatIdStr, NOT_LEADER);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_noTopics_sendNoTopics() {
        long chatId = 10052L;
        long userId = 100052L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveRemoveTopic noTopics sendNoTopics");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, NO_TOPICS);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_topicExists_topicRemoved() {
        long chatId = 10053L;
        long userId = 100053L;
        String chatIdStr = Long.toString(chatId);
        String topic = "Topic";

        register(chatId, userId, "receiveRemoveTopic topicExists topicRemoved");
        createTopic(chatId, userId, topic);

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, String.format(REMOVETOPIC_ASK_FOR_CONFIRMATION, topic));

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, YES_ANSWER));
        verifySendMessage(chatIdStr, REMOVETOPIC_CONFIRMATION_SUCCESS);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_nonExistingTopic_sendNotFoundMessage() {
        long chatId = 10054L;
        long userId = 100054L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveRemoveTopic nonExistingTopic sendNotFoundMessage");
        createTopic(chatId, userId, "Non removable topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "Non existing topic"));
        verifySendMessage(chatIdStr, TOPIC_NOT_FOUND);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_emptyTopicName_askForResending() {
        long chatId = 100055L;
        long userId = 1000055L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveRemoveTopic emptyTopicName askForResending");
        createTopic(chatId, userId, "Some topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, ASK_FOR_RESENDING_TOPIC);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_emptyConfirmation_askForResending() {
        long chatId = 100056L;
        long userId = 1000056L;
        String chatIdStr = Long.toString(chatId);
        String topic = "Looks like topic";

        register(chatId, userId, "receiveRemoveTopic emptyConfirmation askForResending");
        createTopic(chatId, userId, topic);

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, String.format(REMOVETOPIC_ASK_FOR_CONFIRMATION, topic));

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, ASK_FOR_RESENDING_CONFIRMATION);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_wrongConfirmation_askForResending() {
        long chatId = 100057L;
        long userId = 1000057L;
        String chatIdStr = Long.toString(chatId);
        String topic = "Looks like good topic";

        register(chatId, userId, "receiveRemoveTopic wrongConfirmation askForResending");
        createTopic(chatId, userId, topic);

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, String.format(REMOVETOPIC_ASK_FOR_CONFIRMATION, topic));

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "I don't know"));
        verifySendMessage(chatIdStr, ASK_FOR_RESENDING_CONFIRMATION);
    }
}