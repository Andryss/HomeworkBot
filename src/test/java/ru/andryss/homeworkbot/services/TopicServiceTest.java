package ru.andryss.homeworkbot.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.andryss.homeworkbot.repositories.TopicRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class TopicServiceTest {

    @Autowired
    TopicService topicService;

    @Autowired
    TopicRepository topicRepository;

    @AfterEach
    void cleanRepository() {
        topicRepository.deleteAll();
    }

    @Test
    void topicExists_noTopics_returnFalse() {
        boolean isExist = topicService.topicExists("non existing");

        assertThat(isExist, is(false));
    }

    @Test
    void topicExists_topicExist_returnTrue() {
        topicService.createTopic(1L, "1");

        boolean isExist = topicService.topicExists("1");

        assertThat(isExist, is(true));
    }

    @Test
    void listTopics_noTopics_returnEmptyList() {
        List<String> topics = topicService.listTopics();

        assertThat(topics, is(empty()));
    }

    @Test
    void listTopics_someTopics_returnList() {
        topicService.createTopic(1L, "1");
        topicService.createTopic(1L, "2");

        List<String> topics = topicService.listTopics();

        assertThat(topics, containsInAnyOrder("1", "2"));
    }

    @Test
    void removeTopic_noTopics_leaveEmpty() {
        topicService.removeTopic("non existing");

        List<String> topics = topicService.listTopics();
        assertThat(topics, is(empty()));
    }

    @Test
    void removeTopic_someTopics_removeTopic() {
        topicService.createTopic(1L, "1");
        topicService.createTopic(1L, "2");
        topicService.createTopic(1L, "3");

        topicService.removeTopic("2");

        boolean isExist = topicRepository.existsByName("2");
        assertThat(isExist, is(false));

        List<String> topics = topicService.listTopics();
        assertThat(topics, containsInAnyOrder("1", "3"));
    }

    @Test
    void removeTopic_topicNonExist_removeNothing() {
        topicService.createTopic(1L, "1");
        topicService.createTopic(1L, "2");
        topicService.createTopic(1L, "3");

        topicService.removeTopic("non existing");

        List<String> topics = topicService.listTopics();
        assertThat(topics, containsInAnyOrder("1", "2", "3"));
    }

}