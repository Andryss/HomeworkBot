package ru.andryss.homeworkbot.services;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

public interface SubmissionService {
    void uploadSubmission(Long userId, String topicName, String fileId, String extension);
    List<String> listAvailableTopics(Long userId);
    List<String> listUnsolvedTopics(Long userId);
    List<TopicSubmissionsDto> listAllTopicsSubmissions();

    @Data
    @AllArgsConstructor
    class TopicSubmissionsDto {
        private String topicName;
        private List<SubmissionDto> submissions;
    }

    @Data
    @AllArgsConstructor
    class SubmissionDto {
        private String fileId;
        private String extension;
        private String uploadedUserName;
    }
}
