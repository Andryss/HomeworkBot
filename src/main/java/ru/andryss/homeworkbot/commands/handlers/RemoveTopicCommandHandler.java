package ru.andryss.homeworkbot.commands.handlers;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;
import ru.andryss.homeworkbot.commands.utils.MessageUtils;
import ru.andryss.homeworkbot.services.LeaderService;
import ru.andryss.homeworkbot.services.TopicService;
import ru.andryss.homeworkbot.services.UserService;

import java.util.List;
import java.util.Objects;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.KeyboardUtils.buildOneColumnKeyboard;
import static ru.andryss.homeworkbot.commands.utils.KeyboardUtils.buildOneRowKeyboard;

@SuppressWarnings("DuplicatedCode")
@Component
@RequiredArgsConstructor
public class RemoveTopicCommandHandler extends StateCommandHandler<RemoveTopicCommandHandler.UserState> {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/removetopic", COMMAND_REMOVETOPIC);

    private static final int WAITING_FOR_TOPIC_NAME = 0;
    private static final int WAITING_FOR_CONFIRMATION = 1;

    private static final List<List<String>> YES_NO_BUTTONS = buildOneRowKeyboard(YES_ANSWER, NO_ANSWER);

    private final AbsSenderUtils absSenderUtils;
    private final MessageUtils messageUtils;
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
            absSenderUtils.sendMessage(update, sender, REGISTER_FIRST);
            exitForUser(userId);
            return;
        }

        if (!leaderService.isLeader(username)) {
            absSenderUtils.sendMessage(update, sender, NOT_LEADER);
            exitForUser(userId);
            return;
        }

        List<String> topics = topicService.listTopics();

        if (topics.isEmpty()) {
            absSenderUtils.sendMessage(update, sender, NO_TOPICS);
            exitForUser(userId);
        } else {
            absSenderUtils.sendMessage(update, sender, TOPICS_LIST, buildNumberedList(topics));
            absSenderUtils.sendMessageWithKeyboard(update, sender, buildOneColumnKeyboard(topics), REMOVETOPIC_ASK_FOR_TOPIC_NAME);
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
            absSenderUtils.sendMessageWithKeyboard(update, sender, buildOneColumnKeyboard(topics), ASK_FOR_RESENDING_TOPIC);
            return;
        }

        String topic = update.getMessage().getText();

        if (!topics.contains(topic)) {
            absSenderUtils.sendMessageWithKeyboard(update, sender, buildOneColumnKeyboard(topics), TOPIC_NOT_FOUND);
            return;
        }

        UserState userState = getUserState(userId);
        userState.setRemovedTopic(topic);

        absSenderUtils.sendMessageWithKeyboard(update, sender, YES_NO_BUTTONS, REMOVETOPIC_ASK_FOR_CONFIRMATION, topic);
        userState.setState(WAITING_FOR_CONFIRMATION);
    }

    private void onGetConfirmation(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String confirmation = update.getMessage().getText();

        boolean isYes = Objects.equals(confirmation, localize(update, YES_ANSWER));
        boolean isNo = Objects.equals(confirmation, localize(update, NO_ANSWER));

        if (!update.getMessage().hasText() || !isYes && !isNo) {
            absSenderUtils.sendMessageWithKeyboard(update, sender, YES_NO_BUTTONS, ASK_FOR_RESENDING_CONFIRMATION);
            return;
        }

        if (isNo) {
            absSenderUtils.sendMessage(update, sender, REMOVETOPIC_CONFIRMATION_FAILURE);
            exitForUser(userId);
            return;
        }

        topicService.removeTopic(getUserState(userId).getRemovedTopic());
        absSenderUtils.sendMessage(update, sender, REMOVETOPIC_CONFIRMATION_SUCCESS);
        exitForUser(userId);
    }

    private String localize(Update update, String pattern) {
        String lang = update.getMessage().getFrom().getLanguageCode();
        return messageUtils.localize(lang, pattern);
    }
}
