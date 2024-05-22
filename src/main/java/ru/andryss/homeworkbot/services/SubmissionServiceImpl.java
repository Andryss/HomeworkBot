package ru.andryss.homeworkbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.homeworkbot.entities.SubmissionEntity;
import ru.andryss.homeworkbot.entities.UserSubmissionInfo;
import ru.andryss.homeworkbot.entities.TopicEntity;
import ru.andryss.homeworkbot.repositories.SubmissionRepository;
import ru.andryss.homeworkbot.repositories.TopicRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TopicRepository topicRepository;

    @Override
    public Map<String, String> listAvailableTopics(Long userId) {
        List<TopicEntity> allTopics = topicRepository.findAll();
        List<String> submittedTopicsNames = submissionRepository.listTopicsSubmittedBy(userId);
        return allTopics.stream()
                .filter(topic -> !submittedTopicsNames.contains(topic.getName()))
                .collect(toMap(TopicEntity::getName, TopicEntity::getId));
    }

    @Override
    public void uploadSubmission(Long userId, String topicId, String fileId, String extension) {
        SubmissionEntity submission = new SubmissionEntity();
        submission.setFileId(fileId);
        submission.setExtension(extension);
        submission.setUploadedAt(Instant.now());
        submission.setTopicId(topicId);
        submission.setUserId(userId);

        submissionRepository.save(submission);
    }

    @Override
    public List<TopicSubmissionsInfo> listAllSubmissionsGrouped() {
        Map<String, TopicSubmissionsInfo> topicNameToSubmissions = new HashMap<>();

        List<UserSubmissionInfo> allSubmissions = submissionRepository.findAllSubmissions();
        for (UserSubmissionInfo info : allSubmissions) {
            TopicSubmissionsInfo topicSubmissionsInfo = topicNameToSubmissions.computeIfAbsent(info.getTopicName(),
                    name -> new TopicSubmissionsInfo(name, new ArrayList<>()));
            topicSubmissionsInfo.getSubmissions().add(
                    new SubmissionInfo(info.getFileId(), info.getExtension(), info.getUsername())
            );
        }

        return new ArrayList<>(topicNameToSubmissions.values());
    }

    @Override
    public TopicSubmissionsInfo listAllTopicSubmissions(String topic) {
        TopicSubmissionsInfo info = new TopicSubmissionsInfo(topic, new ArrayList<>());

        List<UserSubmissionInfo> submissions = submissionRepository.findAllByTopic(topic);
        for (UserSubmissionInfo submission : submissions) {
            info.getSubmissions().add(
                    new SubmissionInfo(submission.getFileId(), submission.getExtension(), submission.getUsername())
            );
        }
        return info;
    }
}
