package ru.andryss.homeworkbot.services;

import java.util.List;

public interface TopicService {
    void createTopic(Long userId, String topic);
    List<String> listTopics();
}
