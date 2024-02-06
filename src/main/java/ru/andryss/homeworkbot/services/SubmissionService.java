package ru.andryss.homeworkbot.services;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Service to work with user submissions
 */
public interface SubmissionService {
    /**
     * Upload user submission
     *
     * @param userId uploading user
     * @param topicName topic to upload
     * @param fileId telegram file id
     * @param extension extension of uploading file
     */
    void uploadSubmission(Long userId, String topicName, String fileId, String extension);

    /**
     * List available topics for user to upload
     *
     * @param userId user to search
     * @return list of topics
     */
    List<String> listAvailableTopics(Long userId);

    /**
     * List all submissions grouped by topics ({@link TopicSubmissionsDto}).
     *
     * @return all submissions
     */
    List<TopicSubmissionsDto> listAllTopicsSubmissions();

    /**
     * All submission by topic
     */
    @Data
    @AllArgsConstructor
    class TopicSubmissionsDto {
        private String topicName;
        private List<SubmissionDto> submissions;
    }

    /**
     * Describes one submission (user + file)
     */
    @Data
    @AllArgsConstructor
    class SubmissionDto {
        private String fileId;
        private String extension;
        private String uploadedUserName;
    }
}
