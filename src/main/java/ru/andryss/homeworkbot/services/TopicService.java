package ru.andryss.homeworkbot.services;

import java.util.List;

/**
 * Service to work with topics
 */
public interface TopicService {
    /**
     * Checks whether topic exist or not
     *
     * @param topic topic name to check
     * @return true - topic exists
     */
    boolean topicExists(String topic);

    /**
     * Creates topic with given name
     *
     * @param userId creating user
     * @param topic topic name to create
     */
    void createTopic(Long userId, String topic);

    /**
     * List all topics
     *
     * @return list of all created topics
     */
    List<String> listTopics();

    /**
     * Removes topic with given name (and all uploaded submissions)
     *
     * @param topic topic name to remove
     */
    void removeTopic(String topic);
}
