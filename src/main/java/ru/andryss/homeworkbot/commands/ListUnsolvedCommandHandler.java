package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.SubmissionService;
import ru.andryss.homeworkbot.services.UserService;

import java.util.List;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;

@Component
@RequiredArgsConstructor
public class ListUnsolvedCommandHandler extends SingleActionCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/listunsolved", "вывести список нерешенных домашних заданий");

    private final SubmissionService submissionService;
    private final UserService userService;


    @Override
    protected void onReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getChatId();

        if (userService.getUserName(userId).isEmpty()) {
            sendMessage(update, sender, REGISTER_FIRST);
            exitForUser(userId);
            return;
        }

        List<String> unsolvedTopics = submissionService.listAvailableTopics(userId);

        if (unsolvedTopics.isEmpty()) {
            sendMessage(update, sender, LISTUNSOLVED_NO_UNSOLVED_TOPICS);
        } else {
            sendMessage(update, sender, String.format(LISTUNSOLVED_UNSOVLED_TOPICS_LIST, buildNumberedList(unsolvedTopics)));
        }
    }
}
