package ru.andryss.homeworkbot.commands.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.andryss.homeworkbot.services.SubmissionService.SubmissionInfo;
import ru.andryss.homeworkbot.services.SubmissionService.TopicSubmissionsInfo;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Util class for dumping user submissions
 */
@Component
@RequiredArgsConstructor
public class DumpUtils {

    private final AbsSenderUtils absSenderUtils;

    /**
     * Sends submission dump as answer for received event
     *
     * @param update received event
     * @param sender class for executing api calls
     * @param info topic submissions info
     */
    public void sendSolutionsDump(Update update, AbsSender sender, TopicSubmissionsInfo info) throws TelegramApiException, IOException {
        File dumpDir = Files.createTempDirectory("dump").toFile();
        try {
            File zipArchive = new File(dumpDir.getAbsolutePath(), info.getTopicName() + ".zip");
            if (!zipArchive.createNewFile()) {
                throw new IOException("cant create zip archive for dump");
            }
            dump(dumpDir, zipArchive, info, sender);
            absSenderUtils.sendDocument(update, sender, zipArchive);
        } finally {
            FileUtils.deleteQuietly(dumpDir);
        }
    }

    /**
     * Dumps solutions into given zip archive
     *
     * @param dumpDir dump directory
     * @param zipArchive zip archive (inside dump directory) to dump into
     * @param submissionsInfo info about submissions to dump
     * @param sender class for executing api calls
     */
    private void dump(File dumpDir, File zipArchive, TopicSubmissionsInfo submissionsInfo, AbsSender sender) throws IOException, TelegramApiException {
        List<SubmissionInfo> submissions = submissionsInfo.getSubmissions();
        if (submissions.isEmpty()) return;

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipArchive))) {
            for (SubmissionInfo submission : submissions) {
                String submissionFilename = submission.getUploadedUserName() + submission.getExtension();
                File submissionFile = new File(dumpDir.getAbsolutePath(), submissionFilename);
                if (!submissionFile.createNewFile()) {
                    throw new IOException("can't create file " + submissionFile.getAbsolutePath());
                }

                absSenderUtils.downloadFile(sender, submission.getFileId(), submissionFile);

                ZipEntry zipEntry = new ZipEntry(submissionFilename);
                zos.putNextEntry(zipEntry);
                write(submissionFile, zos);
                zos.closeEntry();
            }
        }
    }

    /**
     * Transfers content from file to output stream
     *
     * @param from source file
     * @param to target stream
     */
    private void write(File from, OutputStream to) throws IOException {
        try (FileInputStream fis = new FileInputStream(from)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                to.write(buffer, 0, len);
            }
        }
    }
}
