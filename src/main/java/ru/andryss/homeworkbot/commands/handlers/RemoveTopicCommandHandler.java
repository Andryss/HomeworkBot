package ru.andryss.homeworkbot.commands.handlers;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.LeaderService;
import ru.andryss.homeworkbot.services.TopicService;
import ru.andryss.homeworkbot.services.UserService;

import java.util.List;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessageWithKeyboard;
import static ru.andryss.homeworkbot.commands.utils.KeyboardUtils.buildOneColumnKeyboard;
import static ru.andryss.homeworkbot.commands.utils.KeyboardUtils.buildOneRowKeyboard;

@SuppressWarnings("DuplicatedCode")
@Component
@RequiredArgsConstructor
public class RemoveTopicCommandHandler extends StateCommandHandler<RemoveTopicCommandHandler.UserState> {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/removetopic", "удалить домашнее задание (для старосты)");

    private static final int WAITING_FOR_TOPIC_NAME = 0;
    private static final int WAITING_FOR_CONFIRMATION = 1;

    private static final List<List<String>> YES_NO_BUTTONS = buildOneRowKeyboard(YES_ANSWER, NO_ANSWER);

    private final UserService userService;
    private final LeaderService leaderService;
    private final TopicService topicService;


    @Data
    static class UserState {
        private int state;
        private String removedTopic;
        UserState(int state) {
            this.state = state;
        }
    }

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
            sendMessage(update, sender, NO_TOPICS);
            exitForUser(userId);
        } else {
            sendMessage(update, sender, String.format(TOPICS_LIST, buildNumberedList(topics)));
            sendMessageWithKeyboard(update, sender, REMOVETOPIC_ASK_FOR_TOPIC_NAME, buildOneColumnKeyboard(topics));
            putUserState(userId, new UserState(WAITING_FOR_TOPIC_NAME));
        }
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        UserState userState = getUserState(userId);
        switch (userState.getState()) {
            case WAITING_FOR_TOPIC_NAME -> onGetTopicName(update, sender);
            case WAITING_FOR_CONFIRMATION -> onGetConfirmation(update, sender);
        }
    }

    private void onGetTopicName(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        List<String> topics = topicService.listTopics();

        if (!update.getMessage().hasText()) {
            sendMessageWithKeyboard(update, sender, ASK_FOR_RESENDING_TOPIC, buildOneColumnKeyboard(topics));
            return;
        }

        String topic = update.getMessage().getText();

        if (!topics.contains(topic)) {
            sendMessageWithKeyboard(update, sender, TOPIC_NOT_FOUND, buildOneColumnKeyboard(topics));
            return;
        }

        UserState userState = getUserState(userId);
        userState.setRemovedTopic(topic);

        sendMessageWithKeyboard(update, sender, String.format(REMOVETOPIC_ASK_FOR_CONFIRMATION, topic), YES_NO_BUTTONS);
        userState.setState(WAITING_FOR_CONFIRMATION);
    }

    private void onGetConfirmation(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String confirmation = update.getMessage().getText();

        if (!update.getMessage().hasText() || !confirmation.equals(YES_ANSWER) && !confirmation.equals(NO_ANSWER)) {
            sendMessageWithKeyboard(update, sender, ASK_FOR_RESENDING_CONFIRMATION, YES_NO_BUTTONS);
            return;
        }

        if (confirmation.equals(NO_ANSWER)) {
            sendMessage(update, sender, REMOVETOPIC_CONFIRMATION_FAILURE);
            exitForUser(userId);
            return;
        }

        topicService.removeTopic(getUserState(userId).getRemovedTopic());
        sendMessage(update, sender, REMOVETOPIC_CONFIRMATION_SUCCESS);
        exitForUser(userId);
    }
}
