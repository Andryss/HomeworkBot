package ru.andryss.homeworkbot.commands.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Abstract handler for commands which perform single action
 */
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

    /**
     * Called when command was received
     *
     * @param update received event
     * @param sender class for executing api calls
     */
    protected abstract void onReceived(Update update, AbsSender sender) throws TelegramApiException;
}
