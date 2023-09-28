package ru.andryss.homeworkbot.commands;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommandDispatcher extends TelegramLongPollingBot {

    @Getter
    private final String botUsername;
    private final List<CommandHandler> commandHandlers;

    private final Map<String, CommandHandler> handlerByCommand = new ConcurrentHashMap<>();
    private final Map<Long, String> userToCommand = new ConcurrentHashMap<>();

    private static final String PRIVATE_CHAT_TYPE = "private";
    private static final String HELP_COMMAND = "/help";
    private static final String NO_COMMAND = "";
    private static final String COMMAND_TYPE = "bot_command";

    public CommandDispatcher(
            @Value("${bot.telegram.api.token}") String botToken,
            @Value("${bot.telegram.username}") String botUsername,
            @Autowired List<CommandHandler> commandHandlers
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.commandHandlers = commandHandlers;
    }

    @PostConstruct
    private void init() {
        createHandlerMap();
        initHelpCommand();
    }

    private void createHandlerMap() {
        commandHandlers.forEach(handler -> handlerByCommand.put(handler.getCommandInfo().getName(), handler));
    }

    private void initHelpCommand() {
        handlerByCommand.put(HELP_COMMAND, new HelpCommandHandler(commandHandlers));
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Received: " + update);
        if (!isPrivateMessage(update)) {
            onUnknownUpdate(update); return;
        }

        Message message = update.getMessage();
        Long userId = message.getFrom().getId();

        String userCommand = userToCommand.computeIfAbsent(userId, id -> NO_COMMAND);
        if (!userCommand.equals(NO_COMMAND)) {
            try {
                handlerByCommand.get(userCommand).onUpdateReceived(update, this);
            } catch (TelegramApiException e) {
                onTelegramApiException(update, e);
            }
            return;
        }

        String command = extractCommand(message);
        if (command.equals(NO_COMMAND)) {
            onNoCommandUpdate(update);
            return;
        }

        CommandHandler commandHandler = handlerByCommand.get(command);
        if (commandHandler == null) {
            onUnknownCommandUpdate(update);
            return;
        }

        userToCommand.put(userId, command);

        try {
            commandHandler.onCommandReceived(update, this, () -> userToCommand.put(userId, NO_COMMAND));
        } catch (TelegramApiException e) {
            onTelegramApiException(update, e);
        }
    }

    private boolean isPrivateMessage(Update update) {
        return update.hasMessage() && update.getMessage().getChat().getType().equals(PRIVATE_CHAT_TYPE);
    }

    private void onUnknownUpdate(Update update) {
        System.out.println("Unknown update: " + update); // TODO: replace with logging
    }

    private void onNoCommandUpdate(Update update) {
        System.out.println("No command update: " + update);

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText("К сожалению, я умею отвечать только на команды\n/help");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            onTelegramApiException(update, e);
        }
    }

    private void onUnknownCommandUpdate(Update update) {
        System.out.println("Unknown command update: " + update);
    }

    private void onTelegramApiException(Update update, TelegramApiException e) {
        System.out.println("Exception during update: " + update);
        e.printStackTrace();
    }

    private String extractCommand(Message message) {
        if (!message.hasEntities()) return NO_COMMAND;
        for (MessageEntity messageEntity : message.getEntities()) {
            if (COMMAND_TYPE.equals(messageEntity.getType())) {
                return messageEntity.getText();
            }
        }
        return NO_COMMAND;
    }

    private static class HelpCommandHandler implements CommandHandler {

        @Getter
        private final CommandInfo commandInfo = new CommandInfo(HELP_COMMAND, "вывести список всех команд"); // TODO: extract text to some Message class
        private final String helpMessage;

        HelpCommandHandler(List<CommandHandler> commandHandlers) {
            this.helpMessage = initHelpMessage(commandHandlers);
        }

        private String initHelpMessage(List<CommandHandler> commandHandlers) {
            StringBuilder helpMessageBuilder = new StringBuilder();
            helpMessageBuilder.append(commandInfo.getName()).append(" - ").append(commandInfo.getDescription()).append('\n');
            commandHandlers.forEach(handler -> {
                CommandHandler.CommandInfo info = handler.getCommandInfo();
                helpMessageBuilder.append(info.getName()).append(" - ").append(info.getDescription()).append('\n');
            });
            return helpMessageBuilder.toString();
        }

        @Override
        public void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException {
            AbsSenderUtils.sendMessage(update, sender, helpMessage);
            onExitHandler.run();
        }

        @Override
        public void onUpdateReceived(Update update, AbsSender sender) {

        }
    }
}
