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
        return submissionRepository.listTopicsNotSubmittedBy(userId);
    }

    @Override
    public void uploadSubmission(Long userId, String topicName, String fileId) {
        SubmissionEntity submission = new SubmissionEntity();
        submission.setUser(userRepository.findById(userId).orElseThrow());
        submission.setTopic(topicRepository.findByName(topicName).orElseThrow());
        submission.setFileId(fileId);
        submission.setUploadDatetime(LocalDateTime.now());

        submissionRepository.save(submission);
    }
}
