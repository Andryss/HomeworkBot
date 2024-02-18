package ru.andryss.homeworkbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.homeworkbot.entities.TopicEntity;
import ru.andryss.homeworkbot.entities.UserEntity;
import ru.andryss.homeworkbot.exceptions.NoSuchUserException;
import ru.andryss.homeworkbot.repositories.SubmissionRepository;
import ru.andryss.homeworkbot.repositories.TopicRepository;
import ru.andryss.homeworkbot.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final SubmissionRepository submissionRepository;

    @Override
    public boolean topicExists(String topic) {
        return topicRepository.existsById(topic);
    }

    @Override
    public void createTopic(Long userId, String name) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NoSuchUserException(String.valueOf(userId)));

        TopicEntity topic = new TopicEntity();
        topic.setName(name);
        topic.setCreatedUser(user);
        topic.setCreateDatetime(LocalDateTime.now());

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
