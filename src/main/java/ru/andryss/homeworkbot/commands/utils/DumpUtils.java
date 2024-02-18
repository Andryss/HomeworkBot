package ru.andryss.homeworkbot.commands.utils;

import org.apache.commons.io.FileUtils;
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

import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.downloadFile;
import static ru.andryss.homeworkbot.commands.utils.AbsSenderUtils.sendDocument;

/**
 * Util class for dumping user submissions
 */
public class DumpUtils {

    private DumpUtils() {
        throw new UnsupportedOperationException("util class");
    }

    /**
     * Sends submission dump as answer for received event
     *
     * @param update received event
     * @param sender class for executing api calls
     * @param info topic submissions info
     */
    public static void sendSolutionsDump(Update update, AbsSender sender, TopicSubmissionsInfo info) throws TelegramApiException, IOException {
        File dumpDir = Files.createTempDirectory("dump").toFile();
        try {
            File zipArchive = new File(dumpDir.getAbsolutePath(), info.getTopicName() + ".zip");
            if (!zipArchive.createNewFile()) {
                throw new IOException("cant create zip archive for dump");
            }
            dump(dumpDir, zipArchive, info, sender);
            sendDocument(update, sender, zipArchive);
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
    private static void dump(File dumpDir, File zipArchive, TopicSubmissionsInfo submissionsInfo, AbsSender sender) throws IOException, TelegramApiException {
        List<SubmissionInfo> submissions = submissionsInfo.getSubmissions();
        if (submissions.isEmpty()) return;

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipArchive))) {
            for (SubmissionInfo submission : submissions) {
                String submissionFilename = submission.getUploadedUserName() + submission.getExtension();
                File submissionFile = new File(dumpDir.getAbsolutePath(), submissionFilename);
                if (!submissionFile.createNewFile()) {
                    throw new IOException("can't create file " + submissionFile.getAbsolutePath());
                }


                downloadFile(sender, submission.getFileId(), submissionFile);

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
