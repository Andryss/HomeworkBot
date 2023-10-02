package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.TopicService;

import java.util.List;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;

@Component
@RequiredArgsConstructor
public class ListTopicsCommandHandler extends SingleActionCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/listtopics", "вывести список всех домашних заданий");

    private final TopicService topicService;


    @Override
    protected void onReceived(Update update, AbsSender sender) throws TelegramApiException {
        List<String> topics = topicService.listTopics();

        if (topics.isEmpty()) {
            sendMessage(update, sender, LISTTOPICS_NO_TOPICS);
        } else {
            sendMessage(update, sender, String.format(LISTTOPICS_TOPICS_LIST, createTopicsString(topics)));
        }
    }

    private String createTopicsString(List<String> topics) {
        StringBuilder builder = new StringBuilder();
        for (String topic : topics) {
            builder.append('\n').append("• ").append(topic);
        }
        return builder.toString();
    }
}
