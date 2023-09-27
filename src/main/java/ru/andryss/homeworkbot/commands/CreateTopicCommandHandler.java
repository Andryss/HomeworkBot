package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.LeaderService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Component
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

    @Autowired
    public CreateTopicCommandHandler(LeaderService leaderService) {
        this.leaderService = leaderService;
    }

    @Override
    public void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        if (!leaderService.isLeader(userId)) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText(NOT_LEADER);

            sender.execute(message);
            return;
        }

        executeAskForTopicName(update, sender);
        userToState.put(userId, WAITING_FOR_TOPIC_NAME);
        userToOnExitHandler.put(userId, onExitHandler);
    }

    @Override
    public void onUpdateReceived(Update update, AbsSender sender) throws TelegramApiException {

    }

    private void executeAskForTopicName(Update update, AbsSender sender) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText(ASK_FOR_TOPIC_NAME);

        sender.execute(message);
        Long userId = update.getMessage().getFrom().getId();
        userToState.put(userId, WAITING_FOR_TOPIC_NAME);
    }

    private void executeAskForConfirmation(Update update, AbsSender sender) throws TelegramApiException {
        if (!update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText(ASK_FOR_RESENDING_TOPIC);

            sender.execute(message);
            Long userId = update.getMessage().getFrom().getId();
            userToState.put(userId, WAITING_FOR_TOPIC_NAME);
            return;
        }

        String topic = update.getMessage().getText();
        Long userId = update.getMessage().getFrom().getId();
        userToCreatedTopic.put(userId, topic);

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText(String.format(ASK_FOR_CONFIRMATION, topic));

        sender.execute(message);
        userToState.put(userId, WAITING_FOR_CONFIRMATION);
    }

    private void executeConfirmation(Update update, AbsSender sender) throws TelegramApiException {
        if (!update.getMessage().hasText() || !update.getMessage().getText().equals("да") && !update.getMessage().getText().equals("нет")) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText(ASK_FOR_RESENDING_CONFIRMATION);

            sender.execute(message);
            Long userId = update.getMessage().getFrom().getId();
            userToState.put(userId, WAITING_FOR_CONFIRMATION);
            return;
        }

        String confirmation = update.getMessage().getText();

        if (update.getMessage().getText().equals("нет")) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText(CONFIRMATION_FAILURE);

            sender.execute(message);
            Long userId = update.getMessage().getFrom().getId();
            userToState.remove(userId);
            userToCreatedTopic.remove(userId);
            userToOnExitHandler.remove(userId).run();
            return;
        }

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText(CONFIRMATION_SUCCESS);

        sender.execute(message);
        Long userId = update.getMessage().getFrom().getId();
        createTopic(userId);
        userToState.remove(userId);
        userToCreatedTopic.remove(userId);
        userToOnExitHandler.remove(userId).run();
    }

    private void createTopic(Long userId) {

    }
}
