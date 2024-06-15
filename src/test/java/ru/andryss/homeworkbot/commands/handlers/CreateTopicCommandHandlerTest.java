package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.handlers.Util.*;

@SpringBootTest
class CreateTopicCommandHandlerTest extends CommandHandlerBaseTest {

    @Autowired
    CreateTopicCommandHandler commandHandler;

    @Test
    @SneakyThrows
    void receiveCreateTopic_notRegistered_sendRegisterFirst() {
        long chatId = 10030L;
        long userId = 100030L;

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_notLeader_sendNoLeader() {
        long chatId = 10031L;
        long userId = 100031L;

        register(chatId, userId, "receiveCreateTopic notLeader sendNoLeader");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "not leader"));
        verifySendMessage(NOT_LEADER);
    }

    @ParameterizedTest
    @CsvSource({
            "Some simple topic",
            "Домашнее задание по сборке кибер-гербария",
            "Lorem ipsum",
            "Algentem viridis est herba loquentes",
            "西兰花是绿色的会说话的草",
            "123_321-000",
    })
    @SneakyThrows
    void receiveCreateTopic_validTopic_createTopic(String topic) {
        long chatId = 10032L;
        long userId = 100032L;

        register(chatId, userId, "receiveCreateTopic validTopic createTopic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), CREATETOPIC_ASK_FOR_CONFIRMATION, topic);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "yes"));
        verifySendMessage(CREATETOPIC_CONFIRMATION_SUCCESS);
    }

    @ParameterizedTest
    @CsvSource({
            "!", "#", "?", "$", "%",
            "*", "&", "^", ":", "(",
            ")", "+", "=", "\\", "'",
            "\"", "`",
    })
    @SneakyThrows
    void receiveCreateTopic_invalidCharacters_getWarningMessage(String topic) {
        long chatId = 10033L;
        long userId = 100033L;

        register(chatId, userId, "receiveCreateTopic invalidCharacters getWarningMessage");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(CREATETOPIC_TOPIC_ILLEGAL_CHARACTERS);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_tooLongTopic_getWarningMessage() {
        long chatId = 100034L;
        long userId = 1000034L;
        String topic = "A".repeat(201);

        register(chatId, userId, "receiveCreateTopic tooLongTopic getWarningMessage");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(CREATETOPIC_TOPIC_TOO_MANY_CHARACTERS);

        String newTopic = topic.substring(1);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, newTopic));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), CREATETOPIC_ASK_FOR_CONFIRMATION, newTopic);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "yes"));
        verifySendMessage(CREATETOPIC_CONFIRMATION_SUCCESS);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_topicExist_getWarningMessage() {
        long chatId = 100035L;
        long userId = 1000035L;
        String topic = "Repeated topic";

        register(chatId, userId, "receiveCreateTopic topicExist getWarningMessage");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), CREATETOPIC_ASK_FOR_CONFIRMATION, topic);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "yes"));
        verifySendMessage(CREATETOPIC_CONFIRMATION_SUCCESS);


        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(CREATETOPIC_TOPIC_ALREADY_EXIST);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_confirmationFailure_getFailureMessage() {
        long chatId = 100036L;
        long userId = 1000036L;
        String topic = "Non acceptable topic";

        register(chatId, userId, "receiveCreateTopic confirmationFailure getFailureMessage");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), CREATETOPIC_ASK_FOR_CONFIRMATION, topic);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "no"));
        verifySendMessage(CREATETOPIC_CONFIRMATION_FAILURE);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_emptyTopicName_askForResending() {
        long chatId = 100037L;
        long userId = 1000037L;

        register(chatId, userId, "receiveCreateTopic emptyTopicName askForResending");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(ASK_FOR_RESENDING_TOPIC);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_emptyConfirmation_askForResending() {
        long chatId = 100038L;
        long userId = 1000038L;
        String topic = "Looks like topic";

        register(chatId, userId, "receiveCreateTopic emptyConfirmation askForResending");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), CREATETOPIC_ASK_FOR_CONFIRMATION, topic);

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), ASK_FOR_RESENDING_CONFIRMATION);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_wrongConfirmation_askForResending() {
        long chatId = 100039L;
        long userId = 1000039L;
        String topic = "Looks like good topic";

        register(chatId, userId, "receiveCreateTopic wrongConfirmation askForResending");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), CREATETOPIC_ASK_FOR_CONFIRMATION, topic);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "I don't know"));
        verifySendKeyboard(rowKeyboard(YES_ANSWER, NO_ANSWER), ASK_FOR_RESENDING_CONFIRMATION);
    }
}