package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;
import ru.andryss.homeworkbot.services.UserService;


import static ru.andryss.homeworkbot.commands.Messages.*;

@Component
public class WhoAmICommand implements CommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/whoami", "вывести информацию о пользователе");

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
            AbsSenderUtils.sendMessage(update, sender, REGISTER_FIRST);
        } else {
            AbsSenderUtils.sendMessage(update, sender, String.format(WHOAMI_ANSWER, userName));
        }

        onExitHandler.run();
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) {

    }
}
