package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.UserService;

import static ru.andryss.homeworkbot.commands.Messages.START_ALREADY_REGISTERED;
import static ru.andryss.homeworkbot.commands.Messages.START_ANSWER_FOR_FIRSTNAME_LASTNAME;
import static ru.andryss.homeworkbot.commands.Messages.START_ASK_FOR_FIRSTNAME_LASTNAME;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;

@Component
public class StartCommandHandler extends AbstractCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/start", "зарегистрировать (переименовать) пользователя");

    private final UserService userService;

    @Autowired
    public StartCommandHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void onCommandReceived(Update update, AbsSender sender) throws TelegramApiException {
        sendMessage(update, sender, START_ASK_FOR_FIRSTNAME_LASTNAME);
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String userName = update.getMessage().getText();

        if (userService.userNameExists(userName)) {
            sendMessage(update, sender, START_ALREADY_REGISTERED);
            exitForUser(userId);
        }

        userService.putUserName(userId, userName);
        sendMessage(update, sender, String.format(START_ANSWER_FOR_FIRSTNAME_LASTNAME, userName));
        exitForUser(userId);
    }
}
