package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.TopicService;

import java.util.List;

import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;

@Component
@RequiredArgsConstructor
public class ListTopicsCommandHandler implements CommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/listtopics", "вывести список всех домашних заданий");

    private static final String TOPICS_LIST = "Список домашних заданий: %s";
    private static final String NO_TOPICS = "Нет домашних заданий";

    private final TopicService topicService;


    @Override
    public void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException {
        List<String> topics = topicService.listTopics();

        if (topics.isEmpty()) {
            sendMessage(update, sender, NO_TOPICS);
        } else {
            StringBuilder builder = new StringBuilder();
            for (String topic : topics) {
                builder.append('\n').append("• ").append(topic);
            }
            sendMessage(update, sender, String.format(TOPICS_LIST, builder));
        }

        onExitHandler.run();
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) {

    }
}
