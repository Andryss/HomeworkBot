package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;
import ru.andryss.homeworkbot.services.UserService;

@Component
public class WhoAmICommand implements CommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/whoami", "вывести информацию о пользователе");

    private static final String ANSWER = "ФИО: %s";
    private static final String NOT_REGISTERED = "Вы не зарегистрировались\n/start";

    private final UserService userService;

    @Autowired
    public WhoAmICommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String userName = userService.getUserName(userId);

        if (userName == null) {
            AbsSenderUtils.sendMessage(update, sender, NOT_REGISTERED);
        } else {
            AbsSenderUtils.sendMessage(update, sender, String.format(ANSWER, userName));
        }

        onExitHandler.run();
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) {

    }
}
