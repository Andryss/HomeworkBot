package ru.andryss.homeworkbot.services;

import java.util.List;

public interface TopicService {
    boolean topicExists(String topic);
    void createTopic(Long userId, String topic);
    List<String> listTopics();
}
