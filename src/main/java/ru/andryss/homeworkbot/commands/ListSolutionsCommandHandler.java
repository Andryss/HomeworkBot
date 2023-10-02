package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.SubmissionService;

import java.util.List;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;

@Component
@RequiredArgsConstructor
public class ListSolutionsCommandHandler extends SingleActionCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/listsolutions", "вывести список ваших решений домашних заданий");

    private final SubmissionService submissionService;


    @Override
    protected void onReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getChatId();
        List<String> submittedTopics = submissionService.listSubmittedTopics(userId);

        if (submittedTopics.isEmpty()) {
            sendMessage(update, sender, LISTSOLUTIONS_NO_SUBMITTED_TOPICS);
        } else {
            sendMessage(update, sender, String.format(LISTSOLUTIONS_SUBMITTED_TOPICS_LIST, createTopicsString(submittedTopics)));
        }
    }

    private String createTopicsString(List<String> submittedTopics) {
        StringBuilder builder = new StringBuilder();
        for (String topic : submittedTopics) {
            builder.append('\n').append("• ").append(topic);
        }
        return builder.toString();
    }
}
