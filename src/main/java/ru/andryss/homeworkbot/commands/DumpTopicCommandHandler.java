package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.LeaderService;
import ru.andryss.homeworkbot.services.SubmissionService;
import ru.andryss.homeworkbot.services.SubmissionService.TopicSubmissionsInfo;
import ru.andryss.homeworkbot.services.TopicService;
import ru.andryss.homeworkbot.services.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessageWithKeyboard;
import static ru.andryss.homeworkbot.commands.utils.DumpUtils.sendSolutionsDump;

@Slf4j
@Component
@RequiredArgsConstructor
public class DumpTopicCommandHandler extends AbstractCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/dumptopic", "получить выгрузку всех решений одного домашнего задания (для старосты)");

    private final UserService userService;
    private final LeaderService leaderService;
    private final TopicService topicService;
    private final SubmissionService submissionService;


    @Override
    protected void onCommandReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUserName();

        if (userService.getUserName(userId).isEmpty()) {
            sendMessage(update, sender, REGISTER_FIRST);
            exitForUser(userId);
            return;
        }

        if (!leaderService.isLeader(username)) {
            sendMessage(update, sender, NOT_LEADER);
            exitForUser(userId);
            return;
        }

        List<String> topics = topicService.listTopics();

        if (topics.isEmpty()) {
            sendMessage(update, sender, DUMPTOPIC_NO_TOPICS);
            exitForUser(userId);
        } else {
            StringBuilder builder = new StringBuilder();
            for (String topic : topics) {
                builder.append('\n').append("• ").append(topic);
            }

            sendMessage(update, sender, String.format(DUMPTOPIC_TOPICS_LIST, builder));
            sendMessageWithKeyboard(update, sender, DUMPTOPIC_ASK_FOR_TOPIC_NAME, buildKeyBoard(topics));
        }
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        List<String> topics = topicService.listTopics();

        if (!update.getMessage().hasText()) {
            sendMessageWithKeyboard(update, sender, ASK_FOR_RESENDING_TOPIC, buildKeyBoard(topics));
            return;
        }

        String topic = update.getMessage().getText();

        if (!topics.contains(topic)) {
            sendMessageWithKeyboard(update, sender, TOPIC_NOT_FOUND, buildKeyBoard(topics));
            return;
        }

        TopicSubmissionsInfo submissionsInfo = submissionService.listAllTopicSubmissions(topic);

        if (submissionsInfo.getSubmissions().size() == 0) {
            sendMessage(update, sender, DUMPTOPIC_NO_SUBMISSIONS);
            exitForUser(userId);
            return;
        }

        sendMessage(update, sender, DUMPTOPIC_START_DUMP);

        try {
            sendSolutionsDump(update, sender, submissionsInfo);
        } catch (IOException e) {
            log.error("error occurred during solutions dump", e);
            sendMessage(update, sender, DUMPTOPIC_ERROR_OCCURED);
        }
    }

    private List<List<String>> buildKeyBoard(List<String> rows) {
        List<List<String>> keyboard = new ArrayList<>(rows.size());
        for (String row : rows) {
            keyboard.add(List.of(row));
        }
        return keyboard;
    }
}
