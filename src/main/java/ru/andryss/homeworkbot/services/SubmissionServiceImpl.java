package ru.andryss.homeworkbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.homeworkbot.entities.SubmissionEntity;
import ru.andryss.homeworkbot.repositories.SubmissionRepository;
import ru.andryss.homeworkbot.repositories.TopicRepository;
import ru.andryss.homeworkbot.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    @Override
    public List<String> listAvailableTopics(Long userId) {
        List<String> allTopicsNames = topicRepository.findAllTopicsNames();
        List<String> submittedTopicsNames = submissionRepository.listTopicsSubmittedBy(userId);
        allTopicsNames.removeAll(submittedTopicsNames);
        return allTopicsNames;
    }

    @Override
    public void uploadSubmission(Long userId, String topicName, String fileId, String extension) {
        SubmissionEntity submission = new SubmissionEntity();
        submission.setFileId(fileId);
        submission.setExtension(extension);
        submission.setUploadDatetime(LocalDateTime.now());
        submission.setTopic(topicRepository.findByName(topicName).orElseThrow());
        submission.setUser(userRepository.findById(userId).orElseThrow());

        submissionRepository.save(submission);
    }

    @Override
    public List<String> listSubmittedTopics(Long userId) {
        return submissionRepository.listTopicsSubmittedBy(userId);
    }
}
