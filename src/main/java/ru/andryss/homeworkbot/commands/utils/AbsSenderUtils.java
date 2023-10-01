package ru.andryss.homeworkbot.commands.utils;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AbsSenderUtils {

    public static SendMessage createSendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }

    public static void sendMessage(Update update, AbsSender sender, String text) throws TelegramApiException {
        sender.execute(createSendMessage(update.getMessage().getChatId(), text));
    }

    public static ReplyKeyboard createReplyKeyboard(List<List<String>> buttons) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (List<String> buttonsRow : buttons) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (String button : buttonsRow) {
                keyboardRow.add(button);
            }
            keyboardRows.add(keyboardRow);
        }
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);

        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    public static void sendMessageWithKeyboard(Update update, AbsSender sender, String text, List<List<String>> buttons) throws TelegramApiException {
        SendMessage message = createSendMessage(update.getMessage().getChatId(), text);
        message.setReplyMarkup(createReplyKeyboard(buttons));
        sender.execute(message);
    }


    public static void sendDocument(Update update, AbsSender sender, String fileId, String fileName) throws TelegramApiException {
        if (!(sender instanceof DefaultAbsSender)) throw new TelegramApiException("Not DefaultAbsSender");
        org.telegram.telegrambots.meta.api.objects.File file = sender.execute(createGetFile(fileId));
        File downloadFile = ((DefaultAbsSender) sender).downloadFile(file);
        sender.execute(createSendDocument(update.getMessage().getChatId(), downloadFile, fileName));
    }

    private static GetFile createGetFile(String fileId) {
        GetFile file = new GetFile();
        file.setFileId(fileId);
        return file;
    }

    private static SendDocument createSendDocument(Long chatId, File file, String fileName) {
        SendDocument document = new SendDocument();
        document.setChatId(chatId);
        document.setDocument(new InputFile(file, fileName));
        return document;
    }

}
