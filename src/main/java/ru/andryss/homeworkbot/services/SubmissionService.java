package ru.andryss.homeworkbot.services;

import java.util.List;

public interface SubmissionService {
    void uploadSubmission(Long userId, String topicName, String fileId);
    List<String> listAvailableTopics(Long userId);
}
