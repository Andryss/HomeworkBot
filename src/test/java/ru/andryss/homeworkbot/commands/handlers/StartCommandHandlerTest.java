package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.handlers.Util.createEmptyUpdate;
import static ru.andryss.homeworkbot.commands.handlers.Util.createTextUpdate;

@SpringBootTest
class StartCommandHandlerTest extends CommandHandlerBaseTest {

    @Autowired
    StartCommandHandler commandHandler;

    @ParameterizedTest
    @CsvSource({
            "10000, 100000, User",
            "10001, 100001, Иванов Иван Иванович",
            "10002, 100002, Киб-орг уби-йца",
            "10003, 100003, 我干了一只山羊",
            "10004, 100004, Adam Sandler",
    })
    @SneakyThrows
    void receiveStart_validUsername_registrationSuccess(Long chatId, Long userId, String user) {
        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(START_ASK_FOR_FIRSTNAME_LASTNAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, user));
        verifySendMessage(START_ANSWER_FOR_FIRSTNAME_LASTNAME, user);
    }

    @ParameterizedTest
    @CsvSource({
            "10005, 100005, !#?$%*&",
            "10006, 100006, ^:()_+=",
            "10007, 100007, \\'\"",
    })
    @SneakyThrows
    void receiveStart_invalidCharacters_getWarningMessage(Long chatId, Long userId, String user) {
        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(START_ASK_FOR_FIRSTNAME_LASTNAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, user));
        verifySendMessage(START_ILLEGAL_CHARACTERS);
    }

    @Test
    @SneakyThrows
    void receiveStart_tooLongUsername_getWarningMessage() {
        long chatId = 10008L;
        long userId = 100008L;
        String user = "A".repeat(71);

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(START_ASK_FOR_FIRSTNAME_LASTNAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, user));
        verifySendMessage(START_TOO_MANY_CHARACTERS);

        String newUser = user.substring(1);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, newUser));
        verifySendMessage(START_ANSWER_FOR_FIRSTNAME_LASTNAME, newUser);
    }

    @Test
    @SneakyThrows
    void receiveStart_alreadyRegistered_getWarningMessage() {
        long chatId = 10009L;
        long userId = 100009L;
        String user = "Some some some";

        onCommandReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(START_ASK_FOR_FIRSTNAME_LASTNAME);

        onUpdateReceived(commandHandler, createTextUpdate(chatId, userId, user));
        verifySendMessage(START_ANSWER_FOR_FIRSTNAME_LASTNAME, user);

        long newChat = chatId + 1;
        long newUserId = userId + 1;

        onCommandReceived(commandHandler, createEmptyUpdate(newChat, newUserId));
        verifySendMessage(START_ASK_FOR_FIRSTNAME_LASTNAME);

        onUpdateReceived(commandHandler, createTextUpdate(newChat, newUserId, user));
        verifySendMessage(START_ALREADY_REGISTERED);
    }

}