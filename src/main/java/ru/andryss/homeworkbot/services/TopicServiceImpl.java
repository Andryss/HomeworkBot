package ru.andryss.homeworkbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.homeworkbot.entities.TopicEntity;
import ru.andryss.homeworkbot.repositories.SubmissionRepository;
import ru.andryss.homeworkbot.repositories.TopicRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final SubmissionRepository submissionRepository;

    @Override
    public boolean topicExists(String topic) {
        return topicRepository.existsById(topic);
    }

    @Override
    public void createTopic(Long userId, String name) {
        TopicEntity topic = new TopicEntity();
        topic.setId(UUID.randomUUID().toString());
        topic.setName(name);
        topic.setCreatedBy(userId);
        topic.setCreatedAt(Instant.now());

        topicRepository.save(topic);
    }

    @Override
    public List<String> listTopics() {
        return topicRepository.findAllTopicsNames();
    }

    @Override
    public void removeTopic(String topic) {
        submissionRepository.deleteAllByTopic(topic);
        topicRepository.deleteById(topic);
    }
}
