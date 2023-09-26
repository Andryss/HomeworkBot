package ru.andryss.homeworkbot.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CommandHandler {
    CommandInfo getCommandInfo();
    void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException;
    void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException;

    @Data
    @AllArgsConstructor
    class CommandInfo {
        private String name; // starts with '/' (example "/echo")
        private String description;
    }
}
