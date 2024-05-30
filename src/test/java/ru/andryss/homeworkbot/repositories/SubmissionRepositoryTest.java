package ru.andryss.homeworkbot.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.andryss.homeworkbot.entities.SubmissionEntity;
import ru.andryss.homeworkbot.entities.TopicEntity;
import ru.andryss.homeworkbot.entities.UserEntity;
import ru.andryss.homeworkbot.entities.UserSubmissionInfo;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class SubmissionRepositoryTest {

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void cleanRepository() {
        submissionRepository.deleteAll();
        topicRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void listSubmittedTopics_notSubmitted_returnEmptyList() {
        List<String> submitted = submissionRepository.listTopicsSubmittedBy(1L);

        assertThat(submitted, is(empty()));
    }

    @Test
    void listSubmittedTopics_someSubmitted_returnSubmitted() {
        TopicEntity topic1 = new TopicEntity();
        topic1.setId("1");
        topic1.setName("1");

        TopicEntity topic2 = new TopicEntity();
        topic2.setId("2");
        topic2.setName("2");

        topicRepository.saveAll(List.of(topic1, topic2));

        SubmissionEntity submission1 = new SubmissionEntity();
        submission1.setFileId("1");
        submission1.setTopicId(topic1.getId());
        submission1.setUserId(1L);

        SubmissionEntity submission2 = new SubmissionEntity();
        submission2.setFileId("2");
        submission2.setTopicId(topic2.getId());
        submission2.setUserId(1L);

        submissionRepository.saveAll(List.of(submission1, submission2));

        List<String> submitted = submissionRepository.listTopicsSubmittedBy(1L);

        assertThat(submitted, containsInAnyOrder("1", "2"));
    }

    @Test
    void findAllByTopic_notSubmitted_returnEmptyList() {
        List<UserSubmissionInfo> submissions = submissionRepository.findAllByTopic("non submitted");

        assertThat(submissions, is(empty()));
    }

    @Test
    void findAllByTopic_someSubmitted_returnSubmissions() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setName("1");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setName("2");

        userRepository.saveAll(List.of(user1, user2));

        TopicEntity topic = new TopicEntity();
        topic.setId("1");
        topic.setName("1");

        topicRepository.save(topic);

        SubmissionEntity submission1 = new SubmissionEntity();
        submission1.setTopicId(topic.getId());
        submission1.setFileId("1");
        submission1.setExtension(".txt");
        submission1.setUserId(1L);
        submission1.setUploadedAt(Instant.now());

        SubmissionEntity submission2 = new SubmissionEntity();
        submission2.setTopicId(topic.getId());
        submission2.setFileId("2");
        submission2.setExtension(".avi");
        submission2.setUserId(2L);
        submission2.setUploadedAt(Instant.now());

        submissionRepository.saveAll(List.of(submission1, submission2));

        List<UserSubmissionInfo> submissions = submissionRepository.findAllByTopic("1");

        assertThat(submissions, containsInAnyOrder(
                new UserSubmissionInfo("1", "1", ".txt", "1"),
                new UserSubmissionInfo("1", "2", ".avi", "2")
        ));
    }

    @Test
    void deleteAllByTopic_notSubmitted_leaveEmpty() {
        submissionRepository.deleteAllByTopic("non submitted");

        List<UserSubmissionInfo> submissions = submissionRepository.findAllByTopic("non submitted");
        assertThat(submissions, is(empty()));
    }

    @Test
    void deleteAllByTopic_someSubmitted_deleteAll() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setName("1");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setName("2");

        userRepository.saveAll(List.of(user1, user2));

        TopicEntity topic = new TopicEntity();
        topic.setId("1");
        topic.setName("1");

        topicRepository.save(topic);

        SubmissionEntity submission1 = new SubmissionEntity();
        submission1.setTopicId(topic.getId());
        submission1.setFileId("1");
        submission1.setExtension(".txt");
        submission1.setUserId(1L);
        submission1.setUploadedAt(Instant.now());

        SubmissionEntity submission2 = new SubmissionEntity();
        submission2.setTopicId(topic.getId());
        submission2.setFileId("2");
        submission2.setExtension(".avi");
        submission2.setUserId(2L);
        submission2.setUploadedAt(Instant.now());

        submissionRepository.saveAll(List.of(submission1, submission2));

        submissionRepository.deleteAllByTopic("1");

        List<UserSubmissionInfo> submissions = submissionRepository.findAllByTopic("1");
        assertThat(submissions, is(empty()));
    }

    @Test
    void findAllSubmissions_notSubmitted_returnEmptyList() {
        List<UserSubmissionInfo> submissions = submissionRepository.findAllSubmissions();

        assertThat(submissions, is(empty()));
    }

    @Test
    void findAllSubmissions_someSubmitted_returnAllSubmissions() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setName("1");

        userRepository.save(user);

        TopicEntity topic1 = new TopicEntity();
        topic1.setId("1");
        topic1.setName("1");

        TopicEntity topic2 = new TopicEntity();
        topic2.setId("2");
        topic2.setName("2");

        topicRepository.saveAll(List.of(topic1, topic2));

        SubmissionEntity submission1 = new SubmissionEntity();
        submission1.setTopicId(topic1.getId());
        submission1.setFileId("1");
        submission1.setExtension(".txt");
        submission1.setUserId(1L);
        submission1.setUploadedAt(Instant.now());

        SubmissionEntity submission2 = new SubmissionEntity();
        submission2.setTopicId(topic2.getId());
        submission2.setFileId("2");
        submission2.setExtension(".avi");
        submission2.setUserId(1L);
        submission2.setUploadedAt(Instant.now());

        submissionRepository.saveAll(List.of(submission1, submission2));

        List<UserSubmissionInfo> submissions = submissionRepository.findAllSubmissions();

        assertThat(submissions, containsInAnyOrder(
                new UserSubmissionInfo("1", "1", ".txt", "1"),
                new UserSubmissionInfo("2", "2", ".avi", "1")
        ));
    }

}