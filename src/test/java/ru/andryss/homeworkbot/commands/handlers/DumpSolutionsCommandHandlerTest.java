package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.handlers.Util.createEmptyUpdate;
import static ru.andryss.homeworkbot.commands.handlers.Util.createUserUpdate;

@SpringBootTest
class DumpSolutionsCommandHandlerTest extends CommandHandlerBaseTest {

    @Autowired
    DumpSolutionsCommandHandler commandHandler;

    @BeforeEach
    void clear() {
        clearDatabase();
    }

    @Test
    @SneakyThrows
    void receiveDumpSolutions_notRegistered_sendRegisterFirst() {
        long chatId = 10080L;
        long userId = 100080L;

        onReceived(commandHandler, createEmptyUpdate(chatId, userId));
        verifySendMessage(REGISTER_FIRST);
    }

    @Test
    @SneakyThrows
    void receiveDumpSolutions_notLeader_sendNoLeader() {
        long chatId = 10081L;
        long userId = 100081L;

        register(chatId, userId, "receiveDumpSolutions notLeader sendNoLeader");

        onReceived(commandHandler, createUserUpdate(chatId, userId, "not leader"));
        verifySendMessage(NOT_LEADER);
    }

    @Test
    @SneakyThrows
    void receiveDumpSolutions_noTopicsCreated_sendNoTopics() {
        long chatId = 10082L;
        long userId = 100082L;

        register(chatId, userId, "receiveDumpSolutions noTopicsCreated sendNoTopics");

        onReceived(commandHandler, createUserUpdate(chatId, userId, "God"));
        verifySendMessage(DUMPSOLUTIONS_NO_SUBMISSIONS);
    }
}