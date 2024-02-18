package ru.andryss.homeworkbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.homeworkbot.entities.SubmissionEntity;
import ru.andryss.homeworkbot.entities.TopicEntity;
import ru.andryss.homeworkbot.entities.UserEntity;
import ru.andryss.homeworkbot.exceptions.NoSuchTopicException;
import ru.andryss.homeworkbot.exceptions.NoSuchUserException;
import ru.andryss.homeworkbot.repositories.SubmissionRepository;
import ru.andryss.homeworkbot.repositories.TopicRepository;
import ru.andryss.homeworkbot.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NoSuchUserException(String.valueOf(userId)));
        TopicEntity topic = topicRepository.findById(topicName).orElseThrow(() -> new NoSuchTopicException(topicName));

        SubmissionEntity submission = new SubmissionEntity();
        submission.setFileId(fileId);
        submission.setExtension(extension);
        submission.setUploadDatetime(LocalDateTime.now());
        submission.setTopic(topic);
        submission.setUser(user);

        submissionRepository.save(submission);
    }

    @Override
    public List<TopicSubmissionsInfo> listAllSubmissionsGrouped() {
        Map<String, TopicSubmissionsInfo> topicNameToSubmissions = new HashMap<>();

        List<SubmissionEntity> allSubmissions = submissionRepository.findAll();
        for (SubmissionEntity submission : allSubmissions) {
            String topicName = submission.getTopic().getName();
            TopicSubmissionsInfo topicSubmissionsInfo = topicNameToSubmissions.computeIfAbsent(topicName,
                    name -> new TopicSubmissionsInfo(name, new ArrayList<>()));
            topicSubmissionsInfo.getSubmissions().add(
                    new SubmissionInfo(submission.getFileId(), submission.getExtension(), submission.getUser().getName())
            );
        }

        return new ArrayList<>(topicNameToSubmissions.values());
    }

    @Override
    public TopicSubmissionsInfo listAllTopicSubmissions(String topic) {
        TopicSubmissionsInfo info = new TopicSubmissionsInfo(topic, new ArrayList<>());

        List<SubmissionEntity> submissions = submissionRepository.findAllByTopic(topic);
        for (SubmissionEntity submission : submissions) {
            info.getSubmissions().add(
                    new SubmissionInfo(submission.getFileId(), submission.getExtension(), submission.getUser().getName())
            );
        }
        return info;
    }
}
