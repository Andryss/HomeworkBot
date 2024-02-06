package ru.andryss.homeworkbot.commands.utils;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class for working  with telegram api {@link AbsSender} class
 */
public class AbsSenderUtils {

    private AbsSenderUtils() {
        throw new UnsupportedOperationException("util class");
    }

    /**
     * Sends texts message as answer for received event
     *
     * @param update received event
     * @param sender class for executing api calls
     * @param text text to send
     */
    public static void sendMessage(Update update, AbsSender sender, String text) throws TelegramApiException {
        sender.execute(createSendMessage(update.getMessage().getChatId(), text));
    }

    /**
     * Sends texts message and display keyboard with buttons as answer for received event
     *
     * @param update received event
     * @param sender class for executing api calls
     * @param text text to send
     * @param buttons table of buttons
     */
    public static void sendMessageWithKeyboard(Update update, AbsSender sender, String text, List<List<String>> buttons) throws TelegramApiException {
        SendMessage message = createSendMessage(update.getMessage().getChatId(), text);
        message.setReplyMarkup(createReplyKeyboard(buttons));
        sender.execute(message);
    }

    /**
     * Downloads file by given id
     *
     * @param sender class for executing api calls
     * @param fileId file id to download
     * @param destination file to write downloaded file (must exist)
     */
    public static void downloadFile(AbsSender sender, String fileId, File destination) throws TelegramApiException {
        if (!(sender instanceof DefaultAbsSender)) throw new TelegramApiException("Not DefaultAbsSender");
        org.telegram.telegrambots.meta.api.objects.File file = sender.execute(createGetFile(fileId));
        ((DefaultAbsSender) sender).downloadFile(file, destination);
    }

    /**
     * Sends document as answer for received event
     *
     * @param update received event
     * @param sender class for executing api calls
     * @param file file to send as document
     * @return info about sent message
     */
    public static Message sendDocument(Update update, AbsSender sender, File file) throws TelegramApiException {
        if (!(sender instanceof DefaultAbsSender)) throw new TelegramApiException("Not DefaultAbsSender");
        return sender.execute(createSendDocument(update.getMessage().getChatId(), file, file.getName()));
    }

    /**
     * Creates send message api request
     *
     * @param chatId chat id to send message
     * @param text text of message to send
     * @return send message request
     */
    private static SendMessage createSendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }

    /**
     * Creates object of keyboard which send answers when pressing buttons
     *
     * @param buttons table of buttons (list of rows, each row is a list of cols in row)
     * @return keyboard object
     */
    private static ReplyKeyboard createReplyKeyboard(List<List<String>> buttons) {
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

    /**
     * Creates get file api request
     *
     * @param fileId telegram file identifier
     * @return get file request
     */
    private static GetFile createGetFile(String fileId) {
        GetFile file = new GetFile();
        file.setFileId(fileId);
        return file;
    }

    /**
     * Creates send document api request
     *
     * @param chatId chat id to send document
     * @param file file to send as document
     * @param fileName name to set as filename of document
     * @return send document request
     */
    private static SendDocument createSendDocument(Long chatId, File file, String fileName) {
        SendDocument document = new SendDocument();
        document.setChatId(chatId);
        document.setDocument(new InputFile(file, fileName));
        return document;
    }

}
