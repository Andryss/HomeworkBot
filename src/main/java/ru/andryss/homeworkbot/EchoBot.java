package ru.andryss.homeworkbot;

import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class EchoBot extends AbilityBot {

    private static final String botToken = System.getenv("BOT_TELEGRAM_API_TOKEN");
    private static final String botUsername = System.getenv("BOT_TELEGRAM_USERNAME");

    protected EchoBot() {
        super(botToken, botUsername);
    }

    @Override
    public long creatorId() {
        return 1;
    }

    @Override
    public void onUpdateReceived(Update update) {
        super.onUpdateReceived(update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(update.getMessage().getText());
            try {
                sender.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
