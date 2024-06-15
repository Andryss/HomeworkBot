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

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_notLeader_sendNoLeader() {
        long chatId = 10051L;
        long userId = 100051L;

        register(chatId, userId, "receiveCreateTopic notLeader sendNoLeader");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "not leader"));
        verifySendMessage(NOT_LEADER);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_noTopics_sendNoTopics() {
        long chatId = 10052L;
        long userId = 100052L;

        register(chatId, userId, "receiveRemoveTopic noTopics sendNoTopics");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(NO_TOPICS);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_topicExists_topicRemoved() {
        long chatId = 10053L;
        long userId = 100053L;

        register(chatId, userId, "receiveRemoveTopic topicExists topicRemoved");
        createTopic(chatId, userId, "Topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(TOPICS_LIST, "1) Topic\n");
        verifySendKeyboard(columnKeyboard("Topic"), REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "Topic"));
        verifySendMessage(TOPICS_LIST, "1) Topic\n");
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), REMOVETOPIC_ASK_FOR_CONFIRMATION, "Topic");

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "yes"));
        verifySendMessage(REMOVETOPIC_CONFIRMATION_SUCCESS);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_nonExistingTopic_sendNotFoundMessage() {
        long chatId = 10054L;
        long userId = 100054L;

        register(chatId, userId, "receiveRemoveTopic nonExistingTopic sendNotFoundMessage");
        createTopic(chatId, userId, "Non removable topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(TOPICS_LIST, "1) Non removable topic\n");
        verifySendKeyboard(columnKeyboard("Non removable topic"), REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "Non existing topic"));
        verifySendKeyboard(columnKeyboard("Non removable topic"), TOPIC_NOT_FOUND);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_emptyTopicName_askForResending() {
        long chatId = 100055L;
        long userId = 1000055L;

        register(chatId, userId, "receiveRemoveTopic emptyTopicName askForResending");
        createTopic(chatId, userId, "Some topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(TOPICS_LIST, "1) Some topic\n");
        verifySendKeyboard(columnKeyboard("Some topic"), REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendKeyboard(columnKeyboard("Some topic"), ASK_FOR_RESENDING_TOPIC);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_emptyConfirmation_askForResending() {
        long chatId = 100056L;
        long userId = 1000056L;

        register(chatId, userId, "receiveRemoveTopic emptyConfirmation askForResending");
        createTopic(chatId, userId, "Looks like topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(TOPICS_LIST, "1) Looks like topic\n");
        verifySendKeyboard(columnKeyboard("Looks like topic"), REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "Looks like topic"));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), REMOVETOPIC_ASK_FOR_CONFIRMATION, "Looks like topic");

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), ASK_FOR_RESENDING_CONFIRMATION);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_wrongConfirmation_askForResending() {
        long chatId = 100057L;
        long userId = 1000057L;

        register(chatId, userId, "receiveRemoveTopic wrongConfirmation askForResending");
        createTopic(chatId, userId, "Looks like good topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(TOPICS_LIST, "1) Looks like good topic\n");
        verifySendKeyboard(columnKeyboard("Looks like good topic"), REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "Looks like good topic"));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), REMOVETOPIC_ASK_FOR_CONFIRMATION, "Looks like good topic");

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "I don't know"));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), ASK_FOR_RESENDING_CONFIRMATION);
    }

    @Test
    @SneakyThrows
    void receiveRemoveTopic_confirmationFailure_sendFailureMessage() {
        long chatId = 100058L;
        long userId = 1000058L;

        register(chatId, userId, "receiveRemoveTopic confirmationFailure sendFailureMessage");
        createTopic(chatId, userId, "Look topic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(TOPICS_LIST, "1) Look topic\n");
        verifySendKeyboard(columnKeyboard("Look topic"), REMOVETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "Look topic"));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), REMOVETOPIC_ASK_FOR_CONFIRMATION, "Look topic");

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "no"));
        verifySendMessage(REMOVETOPIC_CONFIRMATION_FAILURE);
    }
}