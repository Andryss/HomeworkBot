package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;
import ru.andryss.homeworkbot.services.LeaderService;
import ru.andryss.homeworkbot.services.TopicService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CreateTopicCommandHandler implements CommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/createtopic", "добавить домашнее задание (для старосты)");

    private static final String NOT_LEADER = "Вы не являетесь старостой";
    private static final String ASK_FOR_TOPIC_NAME = "Пожалуйста, введите название домашнего задания (оно будет отображаться для сдачи):";
    private static final String ASK_FOR_RESENDING_TOPIC = "Пожалуйста, введите название текстом:";
    private static final String ASK_FOR_CONFIRMATION = "Вы уверены, что хотите добавить домашнее задание \"%s\"? (да/нет)";
    private static final String ASK_FOR_RESENDING_CONFIRMATION = "Пожалуйста, выберите \"да\" или \"нет\":";
    private static final String CONFIRMATION_SUCCESS = "Новое домашнее задание добавлено в список";
    private static final String CONFIRMATION_FAILURE = "Не удалось добавить новое домашнее задание";

    private static final int WAITING_FOR_TOPIC_NAME = 0;
    private static final int WAITING_FOR_CONFIRMATION = 1;

    private final Map<Long, Integer> userToState = new ConcurrentHashMap<>();
    private final Map<Long, Runnable> userToOnExitHandler = new ConcurrentHashMap<>();
    private final Map<Long, String> userToCreatedTopic = new ConcurrentHashMap<>();

    private final LeaderService leaderService;
    private final TopicService topicService;

    @Autowired
    public CreateTopicCommandHandler(LeaderService leaderService, TopicService topicService) {
        this.leaderService = leaderService;
        this.topicService = topicService;
    }

    @Override
    public void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        if (!leaderService.isLeader(userId)) {
            AbsSenderUtils.sendMessage(update, sender, NOT_LEADER);
            return;
        }

        onGetCommand(update, sender);
        userToState.put(userId, WAITING_FOR_TOPIC_NAME);
        userToOnExitHandler.put(userId, onExitHandler);
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {

    }

    private void onGetCommand(Update update, AbsSender sender) throws TelegramApiException {
        AbsSenderUtils.sendMessage(update, sender, ASK_FOR_TOPIC_NAME);
        Long userId = update.getMessage().getFrom().getId();
        userToState.put(userId, WAITING_FOR_TOPIC_NAME);
    }

    private void onGetTopicName(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();

        if (!update.getMessage().hasText()) {
            AbsSenderUtils.sendMessage(update, sender, ASK_FOR_RESENDING_TOPIC);
            userToState.put(userId, WAITING_FOR_TOPIC_NAME);
            return;
        }

        String topic = update.getMessage().getText();
        userToCreatedTopic.put(userId, topic);

        AbsSenderUtils.sendMessage(update, sender, String.format(ASK_FOR_CONFIRMATION, topic));
        userToState.put(userId, WAITING_FOR_CONFIRMATION);
    }

    private void onGetConfirmation(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String confirmation = update.getMessage().getText();

        if (!update.getMessage().hasText() || !confirmation.equals("да") && !confirmation.equals("нет")) {
            AbsSenderUtils.sendMessage(update, sender, ASK_FOR_RESENDING_CONFIRMATION);
            userToState.put(userId, WAITING_FOR_CONFIRMATION);
            return;
        }


        if (confirmation.equals("нет")) {
            AbsSenderUtils.sendMessage(update, sender, CONFIRMATION_FAILURE);
            userToState.remove(userId);
            userToCreatedTopic.remove(userId);
            userToOnExitHandler.remove(userId).run();
            return;
        }

        topicService.createTopic(userId, userToCreatedTopic.get(userId));
        AbsSenderUtils.sendMessage(update, sender, CONFIRMATION_SUCCESS);
        userToState.remove(userId);
        userToCreatedTopic.remove(userId);
        userToOnExitHandler.remove(userId).run();
    }
}
