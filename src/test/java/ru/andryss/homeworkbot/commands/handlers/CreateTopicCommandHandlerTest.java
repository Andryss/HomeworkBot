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
        String chatIdStr = Long.toString(chatId);

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_notLeader_sendNoLeader() {
        long chatId = 10031L;
        long userId = 100031L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveCreateTopic notLeader sendNoLeader");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "not leader"));
        verifySendMessage(chatIdStr, NOT_LEADER);
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
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveCreateTopic validTopic createTopic");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, String.format(CREATETOPIC_ASK_FOR_CONFIRMATION, topic));

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, YES_ANSWER));
        verifySendMessage(chatIdStr, CREATETOPIC_CONFIRMATION_SUCCESS);
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
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveCreateTopic invalidCharacters getWarningMessage");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, CREATETOPIC_TOPIC_ILLEGAL_CHARACTERS);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_tooLongTopic_getWarningMessage() {
        long chatId = 100034L;
        long userId = 1000034L;
        String chatIdStr = Long.toString(chatId);
        String topic = "A".repeat(201);

        register(chatId, userId, "receiveCreateTopic tooLongTopic getWarningMessage");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, CREATETOPIC_TOPIC_TOO_MANY_CHARACTERS);

        String newTopic = topic.substring(1);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, newTopic));
        verifySendMessage(chatIdStr, String.format(CREATETOPIC_ASK_FOR_CONFIRMATION, newTopic));

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, YES_ANSWER));
        verifySendMessage(chatIdStr, CREATETOPIC_CONFIRMATION_SUCCESS);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_topicExist_getWarningMessage() {
        long chatId = 100035L;
        long userId = 1000035L;
        String chatIdStr = Long.toString(chatId);
        String topic = "Repeated topic";

        register(chatId, userId, "receiveCreateTopic topicExist getWarningMessage");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, String.format(CREATETOPIC_ASK_FOR_CONFIRMATION, topic));

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, YES_ANSWER));
        verifySendMessage(chatIdStr, CREATETOPIC_CONFIRMATION_SUCCESS);


        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, CREATETOPIC_TOPIC_ALREADY_EXIST);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_confirmationFailure_getFailureMessage() {
        long chatId = 100036L;
        long userId = 1000036L;
        String chatIdStr = Long.toString(chatId);
        String topic = "Non acceptable topic";

        register(chatId, userId, "receiveCreateTopic confirmationFailure getFailureMessage");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, String.format(CREATETOPIC_ASK_FOR_CONFIRMATION, topic));

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, NO_ANSWER));
        verifySendMessage(chatIdStr, CREATETOPIC_CONFIRMATION_FAILURE);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_emptyTopicName_askForResending() {
        long chatId = 100037L;
        long userId = 1000037L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveCreateTopic emptyTopicName askForResending");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, ASK_FOR_RESENDING_TOPIC);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_emptyConfirmation_askForResending() {
        long chatId = 100038L;
        long userId = 1000038L;
        String chatIdStr = Long.toString(chatId);
        String topic = "Looks like topic";

        register(chatId, userId, "receiveCreateTopic emptyConfirmation askForResending");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, String.format(CREATETOPIC_ASK_FOR_CONFIRMATION, topic));

        onUpdateReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, ASK_FOR_RESENDING_CONFIRMATION);
    }

    @Test
    @SneakyThrows
    void receiveCreateTopic_wrongConfirmation_askForResending() {
        long chatId = 100039L;
        long userId = 1000039L;
        String chatIdStr = Long.toString(chatId);
        String topic = "Looks like good topic";

        register(chatId, userId, "receiveCreateTopic wrongConfirmation askForResending");

        onCommandReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(chatIdStr, CREATETOPIC_ASK_FOR_TOPIC_NAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, topic));
        verifySendMessage(chatIdStr, String.format(CREATETOPIC_ASK_FOR_CONFIRMATION, topic));

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, "I don't know"));
        verifySendMessage(chatIdStr, ASK_FOR_RESENDING_CONFIRMATION);
    }
}