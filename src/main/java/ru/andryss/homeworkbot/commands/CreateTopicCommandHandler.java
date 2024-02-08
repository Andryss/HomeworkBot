package ru.andryss.homeworkbot.commands;

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessageWithKeyboard;

@Component
@RequiredArgsConstructor
public class CreateTopicCommandHandler extends AbstractCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/createtopic", "добавить домашнее задание (для старосты)");

    private static final Pattern topicPattern = Pattern.compile("[\\p{L}\\d _\\-]+");

    private static final int WAITING_FOR_TOPIC_NAME = 0;
    private static final int WAITING_FOR_CONFIRMATION = 1;

    private static final List<List<String>> YES_NO_BUTTONS = List.of(List.of(YES_ANSWER, NO_ANSWER));

    private final Map<Long, Integer> userToState = new ConcurrentHashMap<>();
    private final Map<Long, String> userToCreatedTopic = new ConcurrentHashMap<>();

    private final UserService userService;
    private final LeaderService leaderService;
    private final TopicService topicService;


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

        onGetCommand(update, sender);
        userToState.put(userId, WAITING_FOR_TOPIC_NAME);
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        Integer userState = userToState.get(userId);
        switch (userState) {
            case WAITING_FOR_TOPIC_NAME -> onGetTopicName(update, sender);
            case WAITING_FOR_CONFIRMATION -> onGetConfirmation(update, sender);
        }
    }

    private void onGetCommand(Update update, AbsSender sender) throws TelegramApiException {
        sendMessage(update, sender, CREATETOPIC_ASK_FOR_TOPIC_NAME);
        Long userId = update.getMessage().getFrom().getId();
        userToState.put(userId, WAITING_FOR_TOPIC_NAME);
    }

    private void onGetTopicName(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();

        if (!update.getMessage().hasText()) {
            sendMessage(update, sender, ASK_FOR_RESENDING_TOPIC);
            return;
        }

        String topic = update.getMessage().getText().trim();

        if (!topicPattern.matcher(topic).matches()) {
            sendMessage(update, sender, CREATETOPIC_TOPIC_ILLEGAL_CHARACTERS);
            return;
        }

        if (topicService.topicExists(topic)) {
            sendMessage(update, sender, CREATETOPIC_TOPIC_ALREADY_EXIST);
            return;
        }

        userToCreatedTopic.put(userId, topic);

        sendMessageWithKeyboard(update, sender, String.format(CREATETOPIC_ASK_FOR_CONFIRMATION, topic), YES_NO_BUTTONS);
        userToState.put(userId, WAITING_FOR_CONFIRMATION);
    }

    private void onGetConfirmation(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String confirmation = update.getMessage().getText();

        if (!update.getMessage().hasText() || !confirmation.equals(YES_ANSWER) && !confirmation.equals(NO_ANSWER)) {
            sendMessageWithKeyboard(update, sender, ASK_FOR_RESENDING_CONFIRMATION, YES_NO_BUTTONS);
            userToState.put(userId, WAITING_FOR_CONFIRMATION);
            return;
        }


        if (confirmation.equals(NO_ANSWER)) {
            sendMessage(update, sender, CREATETOPIC_CONFIRMATION_FAILURE);
            userToState.remove(userId);
            userToCreatedTopic.remove(userId);
            exitForUser(userId);
            return;
        }

        topicService.createTopic(userId, userToCreatedTopic.get(userId));
        sendMessage(update, sender, CREATETOPIC_CONFIRMATION_SUCCESS);
        userToState.remove(userId);
        userToCreatedTopic.remove(userId);
        exitForUser(userId);
    }
}
