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
     * List all submissions grouped by topics ({@link TopicSubmissionsInfo}).
     *
     * @return all submissions
     */
    List<TopicSubmissionsInfo> listAllSubmissionsGrouped();

    /**
     * List all submissions by given topic ({@link SubmissionInfo}).
     *
     * @return submissions
     */
    TopicSubmissionsInfo listAllTopicSubmissions(String topic);

    /**
     * All submission by topic
     */
    @Data
    @AllArgsConstructor
    class TopicSubmissionsInfo {
        private String topicName;
        private List<SubmissionInfo> submissions;
    }

    /**
     * Describes one submission (user + file)
     */
    @Data
    @AllArgsConstructor
    class SubmissionInfo {
        private String fileId;
        private String extension;
        private String uploadedUserName;
    }
}
