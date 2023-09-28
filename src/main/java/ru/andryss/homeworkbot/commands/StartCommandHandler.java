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

import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;

@Component
public class StartCommandHandler implements CommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/start", "зарегистрировать (переименовать) пользователя");

    private static final String ASK_FOR_USERNAME = "Пожалуйста, введите ФИО, так будут подписаны ваши работы\n(например, \"Иванов Иван Иванович\"):";
    private static final String ANSWER_FOR_USERNAME = "Теперь вы \"%s\"\n/help";

    private final Map<Long, Runnable> userToOnExitHandler = new ConcurrentHashMap<>();

    private final UserService userService;

    @Autowired
    public StartCommandHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException {
        sendMessage(update, sender, ASK_FOR_USERNAME);
        Long userId = update.getMessage().getFrom().getId();
        userToOnExitHandler.put(userId, onExitHandler);
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String userName = update.getMessage().getText();
        userService.putUserName(userId, userName);
        sendMessage(update, sender, String.format(ANSWER_FOR_USERNAME, userName));
        userToOnExitHandler.remove(userId).run();
    }
}
