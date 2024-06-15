package ru.andryss.homeworkbot.commands.handlers;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class Util {

    static Update createEmptyUpdate(Long chatId, Long userId) {
        return createUpdate(chatId, userId, null, null);
    }

    static Update createUserUpdate(Long chatId, Long userId, String username) {
        return createUpdate(chatId, userId, username, null);
    }

    static Update createTextUpdate(Long chatId, Long userId, String text) {
        return createUpdate(chatId, userId, null, text);
    }

    static Update createUpdate(Long chatId, Long userId, String username, String text) {
        Chat chat = new Chat();
        chat.setId(chatId);

        User from = new User();
        from.setId(userId);
        from.setUserName(username);
        from.setLanguageCode("");

        Message message = new Message();
        message.setChat(chat);
        message.setFrom(from);
        message.setText(text);

        Update update = new Update();
        update.setMessage(message);
        return update;
    }

}
