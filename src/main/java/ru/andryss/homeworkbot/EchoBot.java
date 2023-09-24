package ru.andryss.homeworkbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class EchoBot extends TelegramLongPollingBot {

    public EchoBot(@Value("${BOT_TELEGRAM_API_TOKEN}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(update.getMessage().getText());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Value("${BOT_TELEGRAM_USERNAME}")
    private String botUsername;

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
