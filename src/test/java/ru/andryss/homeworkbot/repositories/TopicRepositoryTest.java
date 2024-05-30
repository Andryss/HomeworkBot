package ru.andryss.homeworkbot.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.andryss.homeworkbot.entities.TopicEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class TopicRepositoryTest {

    @Autowired
    TopicRepository topicRepository;

    @AfterEach
    void cleanRepository() {
        topicRepository.deleteAll();
    }

    @Test
    void findAllTopics_emptyRepository_returnEmptyList() {
        List<String> topics = topicRepository.findAllTopicsNames();

        assertThat(topics, is(empty()));
    }

    @Test
    void findAllTopics_topicsExists_returnTopics() {
        TopicEntity topic1 = new TopicEntity();
        topic1.setId("1");
        topic1.setName("1");

        TopicEntity topic2 = new TopicEntity();
        topic2.setId("2");
        topic2.setName("2");

        topicRepository.saveAll(List.of(topic1, topic2));

        List<String> topics = topicRepository.findAllTopicsNames();

        assertThat(topics, containsInAnyOrder("1", "2"));
    }

}