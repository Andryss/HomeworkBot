package ru.andryss.homeworkbot.commands.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;
import ru.andryss.homeworkbot.services.UserService;

import java.util.Optional;

import static ru.andryss.homeworkbot.commands.Messages.*;

@Component
@RequiredArgsConstructor
public class WhoAmICommandHandler extends SingleActionCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/whoami", COMMAND_WHOAMI);

    private final AbsSenderUtils absSenderUtils;
    private final UserService userService;


    @Override
    protected void onReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        Optional<String> userName = userService.getUserName(userId);

        if (userName.isEmpty()) {
            absSenderUtils.sendMessage(update, sender, REGISTER_FIRST);
        } else {
            absSenderUtils.sendMessage(update, sender, WHOAMI_ANSWER, userName.get());
        }
    }
}
