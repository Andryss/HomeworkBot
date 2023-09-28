package ru.andryss.homeworkbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.homeworkbot.entities.TopicEntity;
import ru.andryss.homeworkbot.repositories.TopicRepository;
import ru.andryss.homeworkbot.repositories.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    @Override
    public void createTopic(Long userId, String name) {
        TopicEntity topic = new TopicEntity();
        topic.setName(name);
        topic.setCreatedUser(userRepository.findById(userId).orElseThrow());
        topic.setCreateDatetime(LocalDateTime.now());

        topicRepository.save(topic);
    }
}
