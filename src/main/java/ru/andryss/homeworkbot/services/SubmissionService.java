package ru.andryss.homeworkbot.services;

import java.util.List;

public interface SubmissionService {
    void uploadSubmission(Long userId, String topicName, String fileId, String extension);
    List<String> listAvailableTopics(Long userId);
    List<String> listSubmittedTopics(Long userId);
}
