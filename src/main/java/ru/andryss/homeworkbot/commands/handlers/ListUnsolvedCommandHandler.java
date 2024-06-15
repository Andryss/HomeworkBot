package ru.andryss.homeworkbot.commands.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;
import ru.andryss.homeworkbot.services.SubmissionService;
import ru.andryss.homeworkbot.services.UserService;

import java.util.ArrayList;
import java.util.List;

import static ru.andryss.homeworkbot.commands.Messages.*;

@Component
@RequiredArgsConstructor
public class ListUnsolvedCommandHandler extends SingleActionCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/listunsolved", COMMAND_LISTUNSOLVED);

    private final AbsSenderUtils absSenderUtils;
    private final SubmissionService submissionService;
    private final UserService userService;


    @Override
    protected void onReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();

        if (userService.getUserName(userId).isEmpty()) {
            absSenderUtils.sendMessage(update, sender, REGISTER_FIRST);
            exitForUser(userId);
            return;
        }

        List<String> unsolvedTopics = new ArrayList<>(submissionService.listAvailableTopics(userId).keySet());

        if (unsolvedTopics.isEmpty()) {
            absSenderUtils.sendMessage(update, sender, LISTUNSOLVED_NO_UNSOLVED_TOPICS);
        } else {
            absSenderUtils.sendMessage(update, sender, LISTUNSOLVED_UNSOVLED_TOPICS_LIST, buildNumberedList(unsolvedTopics));
        }
    }
}
