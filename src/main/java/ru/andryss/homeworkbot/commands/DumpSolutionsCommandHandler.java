package ru.andryss.homeworkbot.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.LeaderService;
import ru.andryss.homeworkbot.services.SubmissionService;
import ru.andryss.homeworkbot.services.SubmissionService.SubmissionDto;
import ru.andryss.homeworkbot.services.SubmissionService.TopicSubmissionsDto;
import ru.andryss.homeworkbot.services.UserService;

import java.io.*;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static ru.andryss.homeworkbot.commands.Messages.*;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.*;

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

        try {
            sendSolutionsDump(update, sender);
        } catch (IOException e) {
            log.error("error occurred during solutions dump", e);
            sendMessage(update, sender, DUMPSOLUTIONS_ERROR_OCCURED);
        }
    }

    private void sendSolutionsDump(Update update, AbsSender sender) throws TelegramApiException, IOException {
        List<TopicSubmissionsDto> topicSubmissionsDtoList = submissionService.listAllTopicsSubmissions();

        long submissionsCount = topicSubmissionsDtoList.stream().mapToLong(dto -> dto.getSubmissions().size()).sum();
        if (submissionsCount == 0) {
            sendMessage(update, sender, DUMPSOLUTIONS_NO_SUBMISSIONS);
            return;
        }

        sendMessage(update, sender, DUMPSOLUTIONS_START_DUMP);

        File dumpDir = null;
        try {
            dumpDir = Files.createTempDirectory("dump").toFile();

            File zipArchive = new File(dumpDir.getAbsolutePath(), "archive_" + Instant.now().toEpochMilli() + ".zip");
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipArchive));

            for (TopicSubmissionsDto topicSubmissionsDto : topicSubmissionsDtoList) {
                List<SubmissionDto> submissions = topicSubmissionsDto.getSubmissions();
                if (submissions.isEmpty()) continue;

                String topicName = topicSubmissionsDto.getTopicName();
                File topicDir = new File(dumpDir.getAbsolutePath() + "/" + topicName);
                if (!topicDir.mkdir()) {
                    throw new IOException("can't create dir " + topicDir.getAbsolutePath());
                }

                for (SubmissionDto submission : submissions) {
                    String submissionFilename = submission.getUploadedUserName() + submission.getExtension();
                    File submissionFile = new File(topicDir.getAbsolutePath() + "/" + submissionFilename);
                    if (!submissionFile.createNewFile()) {
                        throw new IOException("can't create file " + submissionFile.getAbsolutePath());
                    }

                    downloadFile(sender, submission.getFileId(), submissionFile);

                    ZipEntry zipEntry = new ZipEntry(topicName + "/" + submissionFilename);
                    zos.putNextEntry(zipEntry);
                    write(submissionFile, zos);
                    zos.closeEntry();
                }
            }

            zos.close();

            sendDocument(update, sender, zipArchive);
        } finally {
            FileUtils.deleteQuietly(dumpDir);
        }
    }

    private static void write(File from, OutputStream to) throws IOException {
        try (FileInputStream fis = new FileInputStream(from)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                to.write(buffer, 0, len);
            }
        }
    }
}
