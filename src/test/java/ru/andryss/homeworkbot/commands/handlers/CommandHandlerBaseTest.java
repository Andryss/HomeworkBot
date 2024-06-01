package ru.andryss.homeworkbot.commands.handlers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static ru.andryss.homeworkbot.commands.handlers.Util.createEmptyUpdate;
import static ru.andryss.homeworkbot.commands.handlers.Util.createTextUpdate;

@SpringBootTest
public class CommandHandlerBaseTest {

    AbsSender absSender;

    @BeforeEach
    void setUpMock() {
        absSender = Mockito.mock(AbsSender.class);
    }

    protected void verifySendMessage(String chatIdStr, String text) throws TelegramApiException {
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(absSender, atLeastOnce()).execute(captor.capture());
        SendMessage msg = captor.getValue();
        assertTrue(msg.getChatId().equals(chatIdStr) && msg.getText().equals(text));
    }

    @Autowired
    StartCommandHandler startCommandHandler;

    protected void register(Long chatId, Long userId, String user) {
        onCommandReceived(startCommandHandler, createEmptyUpdate(chatId, userId));
        onUpdateReceived(startCommandHandler, createTextUpdate(chatId, userId, user));
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

}
