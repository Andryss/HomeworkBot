package ru.andryss.homeworkbot.commands.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.commands.utils.AbsSenderUtils;
import ru.andryss.homeworkbot.commands.utils.DumpUtils;
import ru.andryss.homeworkbot.services.LeaderService;
import ru.andryss.homeworkbot.services.SubmissionService;
import ru.andryss.homeworkbot.services.SubmissionService.TopicSubmissionsInfo;
import ru.andryss.homeworkbot.services.UserService;

import java.io.IOException;
import java.util.List;

import static ru.andryss.homeworkbot.commands.Messages.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DumpSolutionsCommandHandler extends SingleActionCommandHandler {

    @Getter
    private final CommandInfo commandInfo = new CommandInfo("/dumpsolutions", COMMAND_DUMPSOLUTIONS);

    private final AbsSenderUtils absSenderUtils;
    private final DumpUtils dumpUtils;
    private final UserService userService;
    private final LeaderService leaderService;
    private final SubmissionService submissionService;


    @Override
    protected void onReceived(Update update, AbsSender sender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUserName();

        if (userService.getUserName(userId).isEmpty()) {
            absSenderUtils.sendMessage(update, sender, REGISTER_FIRST);
            return;
        }

        if (!leaderService.isLeader(username)) {
            absSenderUtils.sendMessage(update, sender, NOT_LEADER);
            return;
        }

        List<TopicSubmissionsInfo> topicSubmissionsInfoList = submissionService.listAllSubmissionsGrouped();

        long submissionsCount = topicSubmissionsInfoList.stream().mapToLong(dto -> dto.getSubmissions().size()).sum();
        if (submissionsCount == 0) {
            absSenderUtils.sendMessage(update, sender, DUMPSOLUTIONS_NO_SUBMISSIONS);
            return;
        }

        absSenderUtils.sendMessage(update, sender, DUMPSOLUTIONS_START_DUMP);

        try {
            for (TopicSubmissionsInfo submissionsInfo : topicSubmissionsInfoList) {
                dumpUtils.sendSolutionsDump(update, sender, submissionsInfo);
            }

            absSenderUtils.sendMessage(update, sender, DUMPSOLUTIONS_FINISH_DUMP);
        } catch (IOException e) {
            log.error("error occurred during solutions dump", e);
            absSenderUtils.sendMessage(update, sender, DUMPSOLUTIONS_ERROR_OCCURED);
        }
    }
}
