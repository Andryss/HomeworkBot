package ru.andryss.homeworkbot.commands;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Abstract command handler implementing work with onExitHandler
 */
public abstract class AbstractCommandHandler implements CommandHandler {

    private final Map<Long, Runnable> userIdToExitHandler = new ConcurrentHashMap<>();

    @Override
    public void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        userIdToExitHandler.put(userId, onExitHandler);
        onCommandReceived(update, sender);
    }

    /**
     * Invokes onExitHandler for given user
     */
    protected void exitForUser(Long userId) {
        Runnable exitHandler = userIdToExitHandler.remove(userId);
        if (exitHandler == null) return;
        exitHandler.run();
    }

    /**
     * Called when command was received (onExitHandler already handled)
     *
     * @param update received event
     * @param sender class for executing api calls
     */
    protected abstract void onCommandReceived(Update update, AbsSender sender) throws TelegramApiException;
}
