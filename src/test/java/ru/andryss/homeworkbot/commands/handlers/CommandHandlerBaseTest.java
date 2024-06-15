package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static ru.andryss.homeworkbot.commands.handlers.Util.*;

@SpringBootTest
public class CommandHandlerBaseTest {

    @MockBean
    AbsSenderUtils absSenderUtils;

    @MockBean
    AbsSender absSender;

    protected void verifySendMessage(String pattern, Object... args) throws TelegramApiException {
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(absSenderUtils, atLeastOnce()).sendMessage(any(Update.class), same(absSender), keyCaptor.capture(), argsCaptor.capture());
        assertEquals(pattern, keyCaptor.getValue());
        assertArrayEquals(args, argsCaptor.getValue());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void verifySendKeyboard(List<List<String>> buttons, String pattern, Object... args) throws TelegramApiException {
        ArgumentCaptor<List> buttonsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(absSenderUtils, atLeastOnce()).sendMessageWithKeyboard(any(Update.class), same(absSender), buttonsCaptor.capture(), keyCaptor.capture(), argsCaptor.capture());
        assertEquals(buttons, buttonsCaptor.getValue());
        assertEquals(pattern, keyCaptor.getValue());
        assertArrayEquals(args, argsCaptor.getValue());
    }

    protected List<List<String>> rowKeyboard(String... cols) {
        List<String> row = new ArrayList<>(cols.length);
        Collections.addAll(row, cols);
        return List.of(row);
    }

    protected List<List<String>> columnKeyboard(String... rows) {
        List<List<String>> buttons = new ArrayList<>(rows.length);
        for (String row : rows) {
            buttons.add(List.of(row));
        }
        return buttons;
    }

    @SneakyThrows
    protected void onCommandReceived(AbstractCommandHandler handler, Update update) {
        handler.onCommandReceived(update, absSender);
    }

    @SneakyThrows
    protected void onUpdateReceived(AbstractCommandHandler handler, Update update) {
        handler.onUpdateReceived(update, absSender);
    }

    @SneakyThrows
    protected void onReceived(SingleActionCommandHandler handler, Update update) {
        handler.onReceived(update, absSender);
    }

    @Autowired
    StartCommandHandler startCommandHandler;

    protected void register(Long chatId, Long userId, String user) {
        onCommandReceived(startCommandHandler, createEmptyUpdate(chatId, userId));
        onUpdateReceived(startCommandHandler, createTextUpdate(chatId, userId, user));
    }

    @Autowired
    CreateTopicCommandHandler createTopicCommandHandler;

    protected void createTopic(Long chatId, Long userId, String topic) {
        onCommandReceived(createTopicCommandHandler, createUserUpdate(chatId, userId, "God"));
        onUpdateReceived(createTopicCommandHandler, createTextUpdate(chatId, userId, topic));
        onUpdateReceived(createTopicCommandHandler, createTextUpdate(chatId, userId, "yes"));
    }

    @Autowired
    List<JpaRepository<?, ?>> repositories;

    protected void clearDatabase() {
        repositories.forEach(JpaRepository::deleteAll);
    }

}
