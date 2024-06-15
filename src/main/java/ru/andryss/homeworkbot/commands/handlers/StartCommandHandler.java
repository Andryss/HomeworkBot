package ru.andryss.homeworkbot.commands.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;
import ru.andryss.homeworkbot.services.UserService;

import java.util.regex.Pattern;

import static ru.andryss.homeworkbot.commands.Messages.*;

@Component
@RequiredArgsConstructor
public class StartCommandHandler extends AbstractCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/start", COMMAND_START);

    private static final Pattern userNamePattern = Pattern.compile("[\\p{L} \\-]+");

    private final AbsSenderUtils absSenderUtils;
    private final UserService userService;


    @Override
    protected void onCommandReceived(Update update, AbsSender sender) throws TelegramApiException {
        absSenderUtils.sendMessage(update, sender, START_ASK_FOR_FIRSTNAME_LASTNAME);
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String userName = update.getMessage().getText().trim();

        if (!userNamePattern.matcher(userName).matches()) {
            absSenderUtils.sendMessage(update, sender, START_ILLEGAL_CHARACTERS);
            return;
        }

        if (userName.length() > 70) {
            absSenderUtils.sendMessage(update, sender, START_TOO_MANY_CHARACTERS);
            return;
        }

        if (userService.userNameExists(userName)) {
            absSenderUtils.sendMessage(update, sender, START_ALREADY_REGISTERED);
            exitForUser(userId);
            return;
        }

        userService.putUserName(userId, userName);
        absSenderUtils.sendMessage(update, sender, START_ANSWER_FOR_FIRSTNAME_LASTNAME, userName);
        exitForUser(userId);
    }
}
