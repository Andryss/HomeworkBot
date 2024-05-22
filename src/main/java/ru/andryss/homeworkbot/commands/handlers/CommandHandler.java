package ru.andryss.homeworkbot.commands.handlers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Interface describing handler of specific command
 */
public interface CommandHandler {
    /**
     * Returns info about that command
     *
     * @return command info
     */
    CommandInfo getCommandInfo();

    /**
     * Called when command was received
     *
     * @param update received event
     * @param sender class for executing api calls
     * @param onExitHandler handler that must be called when command workflow finishes
     */
    void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException;

    /**
     * Called when next update was received (if onExitHandler was not called)
     *
     * @param update received event
     * @param sender class for executing api calls
     */
    void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException;

    /**
     * Describes command
     */
    @Data
    @AllArgsConstructor
    class CommandInfo {
        /**
         * Command name (e.g. "/echo")
         */
        private String name;
        /**
         * Command description (e.g. "prints help message")
         */
        private String description;
    }
}
