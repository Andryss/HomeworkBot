package ru.andryss.homeworkbot.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class describing one user submission
 */
@Data
@AllArgsConstructor
public class UserSubmissionInfo {
    /**
     * Submitted topic name
     */
    String topicName;
    /**
     * Submission file id
     */
    String fileId;
    /**
     * File extension
     */
    String extension;
    /**
     * Submission author
     */
    String username;
}
