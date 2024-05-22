package ru.andryss.homeworkbot.commands.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.TopicService;
import ru.andryss.homeworkbot.services.UserService;

import java.util.List;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;

@Component
@RequiredArgsConstructor
public class ListTopicsCommandHandler extends SingleActionCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/listtopics", "вывести список всех домашних заданий");

    private final TopicService topicService;
    private final UserService userService;


    @Override
    protected void onReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();

        if (userService.getUserName(userId).isEmpty()) {
            sendMessage(update, sender, REGISTER_FIRST);
            exitForUser(userId);
            return;
        }

        List<String> topics = topicService.listTopics();

        if (topics.isEmpty()) {
            sendMessage(update, sender, NO_TOPICS);
        } else {
            sendMessage(update, sender, String.format(TOPICS_LIST, buildNumberedList(topics)));
        }
    }
}
