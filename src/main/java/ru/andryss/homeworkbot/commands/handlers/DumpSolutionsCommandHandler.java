package ru.andryss.homeworkbot.commands.handlers;

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
import ru.andryss.homeworkbot.services.UserService;

import java.io.IOException;
import java.util.List;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendMessage;
import static ru.andryss.homeworkbot.commands.utils.DumpUtils.sendSolutionsDump;

@Slf4j
@Component
@RequiredArgsConstructor
public class DumpSolutionsCommandHandler extends SingleActionCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/dumpsolutions", "получить выгрузку всех сданных домашних заданий (для старосты)");

    private final UserService userService;
    private final LeaderService leaderService;
    private final SubmissionService submissionService;


    @Override
    protected void onReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUserName();

        if (userService.getUserName(userId).isEmpty()) {
            sendMessage(update, sender, REGISTER_FIRST);
            return;
        }

        if (!leaderService.isLeader(username)) {
            sendMessage(update, sender, NOT_LEADER);
            return;
        }

        List<TopicSubmissionsInfo> topicSubmissionsInfoList = submissionService.listAllSubmissionsGrouped();

        long submissionsCount = topicSubmissionsInfoList.stream().mapToLong(dto -> dto.getSubmissions().size()).sum();
        if (submissionsCount == 0) {
            sendMessage(update, sender, DUMPSOLUTIONS_NO_SUBMISSIONS);
            return;
        }

        sendMessage(update, sender, DUMPSOLUTIONS_START_DUMP);

        try {
            for (TopicSubmissionsInfo submissionsInfo : topicSubmissionsInfoList) {
                sendSolutionsDump(update, sender, submissionsInfo);
            }

            sendMessage(update, sender, DUMPSOLUTIONS_FINISH_DUMP);
        } catch (IOException e) {
            log.error("error occurred during solutions dump", e);
            sendMessage(update, sender, DUMPSOLUTIONS_ERROR_OCCURED);
        }
    }
}
