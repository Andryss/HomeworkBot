package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.UserService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;

@Component
public class StartCommandHandler implements CommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/start", "зарегистрировать (переименовать) пользователя");

    private final Map<Long, Runnable> userToOnExitHandler = new ConcurrentHashMap<>();

    private final UserService userService;

    @Autowired
    public StartCommandHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException {
        sendMessage(update, sender, START_ASK_FOR_FIRSTNAME_LASTNAME);
        Long userId = update.getMessage().getFrom().getId();
        userToOnExitHandler.put(userId, onExitHandler);
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String userName = update.getMessage().getText();
        userService.putUserName(userId, userName);
        sendMessage(update, sender, String.format(START_ANSWER_FOR_FIRSTNAME_LASTNAME, userName));
        userToOnExitHandler.remove(userId).run();
    }
}
