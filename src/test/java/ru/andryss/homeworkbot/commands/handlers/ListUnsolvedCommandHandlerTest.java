package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.handlers.Util.createEmptyUpdate;

@SpringBootTest
class ListUnsolvedCommandHandlerTest extends CommandHandlerBaseTest {

    @Autowired
    ListUnsolvedCommandHandler commandHandler;

    @BeforeEach
    void clear() {
        clearDatabase();
    }

    @Test
    @SneakyThrows
    void receiveListUnsolved_notRegistered_sendRegisterFirst() {
        long chatId = 10060L;
        long userId = 100060L;
        String chatIdStr = Long.toString(chatId);

        onReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveListUnsolved_noTopicsCreated_sendNoTopics() {
        long chatId = 10061L;
        long userId = 100061L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveListUnsolved noTopicsCreated sendNoTopics");

        onReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, LISTUNSOLVED_NO_UNSOLVED_TOPICS);
    }

    @Test
    @SneakyThrows
    void receiveListUnsolved_someTopicsCreated_sendTopics() {
        long chatId = 10062L;
        long userId = 100062L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveListUnsolved someTopicsCreated sendTopics");

        createTopic(chatId, userId, "First topic");
        createTopic(chatId, userId, "Second topic");

        onReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, String.format(LISTUNSOLVED_UNSOVLED_TOPICS_LIST, "1) First topic\n2) Second topic\n"));
    }
}