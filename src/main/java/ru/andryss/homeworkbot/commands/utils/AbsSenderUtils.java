package ru.andryss.homeworkbot.commands.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class AbsSenderUtils {

    public static void sendMessage(Update update, AbsSender sender, String text) throws TelegramApiException {
        sendMessage(update.getMessage(), sender, text);
    }

    public static void sendMessage(Message message, AbsSender sender, String text) throws TelegramApiException {
        sendMessage(message.getChatId(), sender, text);
    }

    public static void sendMessage(Long chatId, AbsSender sender, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        sender.execute(message);
    }
}
