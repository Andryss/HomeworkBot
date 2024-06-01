package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static ru.andryss.homeworkbot.commands.Messages.REGISTER_FIRST;
import static ru.andryss.homeworkbot.commands.Messages.WHOAMI_ANSWER;
import static ru.andryss.homeworkbot.commands.handlers.Util.createEmptyUpdate;

@SpringBootTest
class WhoAmICommandHandlerTest extends CommandHandlerBaseTest {

    @Autowired
    WhoAmICommandHandler commandHandler;

    @Test
    @SneakyThrows
    void receiveWhoAmI_notRegistered_sendRegisterFirst() {
        long chatId = 10020L;
        long userId = 100020L;
        String chatIdStr = Long.toString(chatId);

        onReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveWhoAmI_registered_sendAnswer() {
        long chatId = 10021L;
        long userId = 100021L;
        String chatIdStr = Long.toString(chatId);
        String user = "receiveWhoAmI registered sendAnswer";

        register(chatId, userId, user);

        onReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, String.format(WHOAMI_ANSWER, user));
    }

}