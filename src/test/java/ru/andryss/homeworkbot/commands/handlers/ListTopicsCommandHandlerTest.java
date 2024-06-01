package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.handlers.Util.createEmptyUpdate;

@SpringBootTest
class ListTopicsCommandHandlerTest extends CommandHandlerBaseTest {

    @Autowired
    ListTopicsCommandHandler commandHandler;

    @BeforeEach
    void clear() {
        clearDatabase();
    }

    @Test
    @SneakyThrows
    void receiveListTopics_notRegistered_sendRegisterFirst() {
        long chatId = 10040L;
        long userId = 100040L;
        String chatIdStr = Long.toString(chatId);

        onReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveListTopics_noTopicsCreated_sendNoTopics() {
        long chatId = 10041L;
        long userId = 100041L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveListTopics noTopicsCreated sendNoTopics");

        onReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, NO_TOPICS);
    }

    @Test
    @SneakyThrows
    void receiveListTopics_someTopicsCreated_sendTopics() {
        long chatId = 10042L;
        long userId = 100042L;
        String chatIdStr = Long.toString(chatId);

        register(chatId, userId, "receiveListTopics someTopicsCreated sendTopics");

        createTopic(chatId, userId, "First topic");
        createTopic(chatId, userId, "Second topic");

        onReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(chatIdStr, String.format(TOPICS_LIST, "1) First topic\n2) Second topic\n"));
    }
}