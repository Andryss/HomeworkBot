package ru.andryss.homeworkbot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class SingleActionCommandHandler extends AbstractCommandHandler {

    @Override
    protected void onCommandReceived(Update update, AbsSender sender) throws TelegramApiException {
        onReceived(update, sender);
        Long userId = update.getMessage().getFrom().getId();
        exitForUser(userId);
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) {
        /* unreachable */
    }

    protected abstract void onReceived(Update update, AbsSender sender) throws TelegramApiException;
}
