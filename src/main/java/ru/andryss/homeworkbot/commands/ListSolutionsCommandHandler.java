package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.SubmissionService;

import java.util.List;

import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;

@Component
@RequiredArgsConstructor
public class ListSolutionsCommandHandler implements CommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/listsolutions", "вывести список ваших решений домашних заданий");

    private static final String TOPICS_LIST = "Список домашних заданий с загруженным решением: %s";
    private static final String NO_TOPICS = "Вы еще не загрузили ни одного домашнего задания\n/uploadsolution";

    private final SubmissionService submissionService;


    @Override
    public void onCommandReceived(Update update, AbsSender sender, Runnable onExitHandler) throws TelegramApiException {
        Long userId = update.getMessage().getChatId();
        List<String> submittedTopics = submissionService.listSubmittedTopics(userId);

        if (submittedTopics.isEmpty()) {
            sendMessage(update, sender, NO_TOPICS);
        } else {
            StringBuilder builder = new StringBuilder();
            for (String topic : submittedTopics) {
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
