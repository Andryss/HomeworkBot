package ru.andryss.homeworkbot.commands.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class for working  with telegram api {@link AbsSender} class
 */
@Component
@RequiredArgsConstructor
public class AbsSenderUtils {

    private final MessageUtils messageUtils;

    /**
     * Sends texts message as answer for received event
     *
     * @param update received event
     * @param sender class for executing api calls
     * @param key key to send
     * @param args arguments for message
     */
    public void sendMessage(Update update, AbsSender sender, String key, Object... args) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String lang = update.getMessage().getFrom().getLanguageCode();
        sender.execute(createSendMessage(chatId, lang, null, key, args));
    }

    /**
     * Sends texts message and display keyboard with buttons as answer for received event
     *
     * @param update received event
     * @param sender class for executing api calls
     * @param buttons table of buttons
     * @param key key to send
     * @param args arguments for message
     */
    public void sendMessageWithKeyboard(Update update, AbsSender sender, List<List<String>> buttons, String key, Object... args) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String lang = update.getMessage().getFrom().getLanguageCode();
        sender.execute(createSendMessage(chatId, lang, buttons, key, args));
    }

    /**
     * Downloads file by given id
     *
     * @param sender class for executing api calls
     * @param fileId file id to download
     * @param destination file to write downloaded file (must exist)
     */
    public void downloadFile(AbsSender sender, String fileId, File destination) throws TelegramApiException {
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
    public Message sendDocument(Update update, AbsSender sender, File file) throws TelegramApiException {
        if (!(sender instanceof DefaultAbsSender)) throw new TelegramApiException("Not DefaultAbsSender");
        return sender.execute(createSendDocument(update.getMessage().getChatId(), file, file.getName()));
    }

    /**
     * Creates send message api request
     *
     * @param chatId chat identifier
     * @param lang language code (e.g. "ru")
     * @param buttons optional buttons to show
     * @param key message key (starts with "") or message
     * @param args arguments for message
     * @return created send message object
     */
    private SendMessage createSendMessage(Long chatId, String lang, List<List<String>> buttons, String key, Object... args) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageUtils.localize(lang, key, args));
        message.setReplyMarkup(createReplyKeyboard(lang, buttons));
        return message;
    }

    /**
     * Creates object of keyboard which send answers when pressing buttons
     *
     * @param lang language code (e.g. "ru")
     * @param buttons table of buttons (list of rows, each row is a list of cols in row)
     * @return keyboard object
     */
    private ReplyKeyboard createReplyKeyboard(String lang, List<List<String>> buttons) {
        if (buttons == null) {
            return new ReplyKeyboardRemove(true);
        }
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (List<String> buttonsRow : buttons) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (String button : buttonsRow) {
                keyboardRow.add(messageUtils.localize(lang, button));
            }
            keyboardRows.add(keyboardRow);
        }
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);

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
    private GetFile createGetFile(String fileId) {
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
    private SendDocument createSendDocument(Long chatId, File file, String fileName) {
        SendDocument document = new SendDocument();
        document.setChatId(chatId);
        document.setDocument(new InputFile(file, fileName));
        return document;
    }
}
