package ru.andryss.homeworkbot.commands;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.handlers.CommandHandler;
import ru.andryss.homeworkbot.commands.handlers.SingleActionCommandHandler;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;
import ru.andryss.homeworkbot.commands.utils.MessageUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.andryss.homeworkbot.commands.Messages.*;

/**
 * Entrypoint for all received events.
 * Extracts command from message and dispatches event to corresponding handler.
 */
@Slf4j
@Component
public class CommandDispatcher extends TelegramLongPollingBot {

    @Getter
    private final String botUsername;
    private final List<CommandHandler> commandHandlers;

    private final Map<String, CommandHandler> handlerByCommand = new ConcurrentHashMap<>();
    private final Map<Long, String> userToCommand = new ConcurrentHashMap<>();

    private static final String NO_COMMAND = "";
    
    private final AbsSenderUtils absSenderUtils;
    private final MessageUtils messageUtils;

    public CommandDispatcher(
            @Value("${bot.telegram.api.token}") String botToken,
            @Value("${bot.telegram.username}") String botUsername,
            @Autowired List<CommandHandler> commandHandlers,
            @Autowired AbsSenderUtils absSenderUtils,
            @Autowired MessageUtils messageUtils
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.commandHandlers = commandHandlers;
        this.absSenderUtils = absSenderUtils;
        this.messageUtils = messageUtils;
    }

    @PostConstruct
    private void init() {
        commandHandlers.forEach(handler -> handlerByCommand.put(handler.getCommandInfo().getName(), handler));
        handlerByCommand.put("/help", new HelpCommandHandler(absSenderUtils, messageUtils, commandHandlers));
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received: {}", update);

        if (!update.hasMessage() || !update.getMessage().getChat().getType().equals("private")) {
            log.warn("Unknown update {}, skipping", update.getUpdateId());
            return;
        }

        try {
            Message message = update.getMessage();
            Long userId = message.getFrom().getId();

            String command = extractCommand(message);
            if (command.equals(NO_COMMAND)) {
                String userCommand = userToCommand.computeIfAbsent(userId, id -> NO_COMMAND);
                if (userCommand.equals(NO_COMMAND)) {
                    log.warn("No command update {}", update.getUpdateId());
                    absSenderUtils.sendMessage(update, this, DISPATCHER_NO_COMMAND);
                    return;
                }

                try {
                    handlerByCommand.get(userCommand).onUpdateReceived(update, this);
                } catch (Exception e) {
                    log.error(String.format("Exception during update %s", update.getUpdateId()), e);
                    absSenderUtils.sendMessage(update, this, DISPATCHER_HANDLER_ERROR);
                }
                return;
            }

            CommandHandler commandHandler = handlerByCommand.get(command);
            if (commandHandler == null) {
                log.warn("Unknown command {} update {}", command, update.getUpdateId());
                absSenderUtils.sendMessage(update, this, DISPATCHER_UNKNOWN_COMMAND);
                return;
            }

            userToCommand.put(userId, command);

            commandHandler.onCommandReceived(update, this, () -> userToCommand.put(userId, NO_COMMAND));

        } catch (Exception e) {
            log.error(String.format("Unhandled exception during update %s", update.getUpdateId()), e);
            try {
                absSenderUtils.sendMessage(update, this, DISPATCHER_ERROR);
            } catch (TelegramApiException ex) {
                // sadness :(
            }
        }
    }

    private String extractCommand(Message message) {
        if (!message.hasEntities()) return NO_COMMAND;
        for (MessageEntity messageEntity : message.getEntities()) {
            if (messageEntity.getType().equals("bot_command")) {
                return messageEntity.getText();
            }
        }
        return NO_COMMAND;
    }

    @RequiredArgsConstructor
    private static class HelpCommandHandler extends SingleActionCommandHandler {

        @Getter
        private final CommandInfo commandInfo = new CommandInfo("/help", COMMAND_HELP);

        private final AbsSenderUtils absSenderUtils;
        private final MessageUtils messageUtils;
        private final List<CommandHandler> commandHandlers;

        @Override
        protected void onReceived(Update update, AbsSender sender) throws TelegramApiException {
            String lang = update.getMessage().getFrom().getLanguageCode();

            StringBuilder helpMessage = new StringBuilder();
            String helpDescription = messageUtils.localize(lang, commandInfo.getDescription());
            helpMessage.append(commandInfo.getName()).append(" - ").append(helpDescription).append('\n');

            commandHandlers.forEach(handler -> {
                CommandHandler.CommandInfo info = handler.getCommandInfo();
                String description = messageUtils.localize(lang, info.getDescription());
                helpMessage.append(info.getName()).append(" - ").append(description).append('\n');
            });

            absSenderUtils.sendMessage(update, sender, helpMessage.toString());
        }
    }
}
