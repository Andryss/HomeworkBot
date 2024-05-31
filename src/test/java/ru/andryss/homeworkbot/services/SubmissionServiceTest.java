package ru.andryss.homeworkbot.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.andryss.homeworkbot.entities.SubmissionEntity;
import ru.andryss.homeworkbot.entities.TopicEntity;
import ru.andryss.homeworkbot.entities.UserEntity;
import ru.andryss.homeworkbot.repositories.SubmissionRepository;
import ru.andryss.homeworkbot.repositories.TopicRepository;
import ru.andryss.homeworkbot.repositories.UserRepository;
import ru.andryss.homeworkbot.services.SubmissionService.SubmissionInfo;
import ru.andryss.homeworkbot.services.SubmissionService.TopicSubmissionsInfo;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class SubmissionServiceTest {

    @Autowired
    SubmissionService submissionService;

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
    void listAvailableTopics_emptyRepository_returnEmptyMap() {
        Map<String, String> availableTopics = submissionService.listAvailableTopics(1L);

        assertThat(availableTopics, is(anEmptyMap()));
    }

    @Test
    void listAvailableTopics_nothingUploaded_returnAll() {
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

        Map<String, String> availableTopics = submissionService.listAvailableTopics(1L);

        assertThat(availableTopics, is(aMapWithSize(2)));
        assertThat(availableTopics.get("1"), is(equalTo("1")));
        assertThat(availableTopics.get("2"), is(equalTo("2")));
    }

    @Test
    void listAvailableTopics_someUploaded_returnNotUploaded() {
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

        TopicEntity topic3 = new TopicEntity();
        topic3.setId("3");
        topic3.setName("3");

        topicRepository.saveAll(List.of(topic1, topic2, topic3));

        SubmissionEntity submission = new SubmissionEntity();
        submission.setTopicId(topic1.getId());
        submission.setFileId("1");
        submission.setExtension(".txt");
        submission.setUserId(user.getId());
        submission.setUploadedAt(Instant.now());

        submissionRepository.save(submission);

        Map<String, String> availableTopics = submissionService.listAvailableTopics(1L);

        assertThat(availableTopics, is(aMapWithSize(2)));
        assertThat(availableTopics.get("2"), is(equalTo("2")));
        assertThat(availableTopics.get("3"), is(equalTo("3")));
    }

    @Test
    void uploadSubmission_emptyRepository_returnEmptyMap() {
        submissionService.uploadSubmission(1L, "1", "1", ".txt");

        Map<String, String> availableTopics = submissionService.listAvailableTopics(1L);

        assertThat(availableTopics, is(anEmptyMap()));
    }

    @Test
    void uploadSubmission_nothingUploaded_createSubmissions() {
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

        TopicEntity topic3 = new TopicEntity();
        topic3.setId("3");
        topic3.setName("3");

        topicRepository.saveAll(List.of(topic1, topic2, topic3));

        submissionService.uploadSubmission(user.getId(), topic1.getId(), "1", ".txt");

        Map<String, String> availableTopics = submissionService.listAvailableTopics(1L);

        assertThat(availableTopics, is(aMapWithSize(2)));
        assertThat(availableTopics.get("2"), is(equalTo("2")));
        assertThat(availableTopics.get("3"), is(equalTo("3")));
    }

    @Test
    void listAllTopicSubmissions_nothingUploaded_returnEmptySubmissions() {
        TopicEntity topic = new TopicEntity();
        topic.setId("1");
        topic.setName("1");

        topicRepository.save(topic);

        TopicSubmissionsInfo submissions = submissionService.listAllTopicSubmissions("1");

        assertThat(submissions.getTopicName(), is(equalTo("1")));
        assertThat(submissions.getSubmissions(), is(empty()));
    }

    @Test
    void listAllTopicSubmissions_someUploaded_returnSubmissions() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setName("1");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setName("2");

        UserEntity user3 = new UserEntity();
        user3.setId(3L);
        user3.setName("3");

        userRepository.saveAll(List.of(user1, user2, user3));

        TopicEntity topic = new TopicEntity();
        topic.setId("1");
        topic.setName("1");

        topicRepository.save(topic);

        submissionService.uploadSubmission(user1.getId(), topic.getId(), "1", ".txt");
        submissionService.uploadSubmission(user2.getId(), topic.getId(), "2", ".avi");

        TopicSubmissionsInfo submissions = submissionService.listAllTopicSubmissions("1");

        assertThat(submissions.getTopicName(), is(equalTo("1")));

        List<SubmissionInfo> infos = submissions.getSubmissions();

        assertThat(infos, hasSize(2));
        assertThat(infos, containsInAnyOrder(
                new SubmissionInfo("1", ".txt", "1"),
                new SubmissionInfo("2", ".avi", "2")
        ));
    }

    @Test
    void listAllTopicSubmissions_allUploaded_returnAll() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setName("1");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setName("2");

        UserEntity user3 = new UserEntity();
        user3.setId(3L);
        user3.setName("3");

        userRepository.saveAll(List.of(user1, user2, user3));

        TopicEntity topic = new TopicEntity();
        topic.setId("1");
        topic.setName("1");

        topicRepository.save(topic);

        submissionService.uploadSubmission(user1.getId(), topic.getId(), "1", ".txt");
        submissionService.uploadSubmission(user2.getId(), topic.getId(), "2", ".avi");
        submissionService.uploadSubmission(user3.getId(), topic.getId(), "3", ".mp3");

        TopicSubmissionsInfo submissions = submissionService.listAllTopicSubmissions("1");

        assertThat(submissions.getTopicName(), is(equalTo("1")));

        List<SubmissionInfo> infos = submissions.getSubmissions();

        assertThat(infos, hasSize(3));
        assertThat(infos, containsInAnyOrder(
                new SubmissionInfo("1", ".txt", "1"),
                new SubmissionInfo("2", ".avi", "2"),
                new SubmissionInfo("3", ".mp3", "3")
        ));
    }

    @Test
    void listAllSubmissionsGrouped_nothingUploaded_returnEmptyList() {
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

        List<TopicSubmissionsInfo> submissionsInfos = submissionService.listAllSubmissionsGrouped();

        assertThat(submissionsInfos, hasSize(0));
    }

    @Test
    void listAllSubmissionsGrouped_someUserSubmitted_returnSubmissions() {
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

        submissionService.uploadSubmission(1L, "1", "1", ".txt");

        List<TopicSubmissionsInfo> submissionsInfos = submissionService.listAllSubmissionsGrouped();
        assertThat(submissionsInfos, hasSize(1));

        TopicSubmissionsInfo info = submissionsInfos.get(0);
        assertThat(info.getTopicName(), is(equalTo("1")));

        List<SubmissionInfo> submissions = info.getSubmissions();
        assertThat(submissions, hasSize(1));
        assertThat(submissions, containsInAnyOrder(
                new SubmissionInfo("1", ".txt", "1")
        ));
    }

    @Test
    void listAllSubmissionsGrouped_someTopicsSubmitted_returnSubmissions() {
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

        submissionService.uploadSubmission(1L, "1", "1", ".txt");
        submissionService.uploadSubmission(1L, "2", "2", ".mp4");

        List<TopicSubmissionsInfo> submissionsInfos = submissionService.listAllSubmissionsGrouped();
        assertThat(submissionsInfos, hasSize(2));

        TopicSubmissionsInfo info0 = submissionsInfos.get(0);
        assertThat(info0.getTopicName(), is(equalTo("1")));

        List<SubmissionInfo> submissions0 = info0.getSubmissions();
        assertThat(submissions0, hasSize(1));
        assertThat(submissions0, containsInAnyOrder(
                new SubmissionInfo("1", ".txt", "1")
        ));

        TopicSubmissionsInfo info1 = submissionsInfos.get(1);
        assertThat(info1.getTopicName(), is(equalTo("2")));

        List<SubmissionInfo> submissions1 = info1.getSubmissions();
        assertThat(submissions1, hasSize(1));
        assertThat(submissions1, containsInAnyOrder(
                new SubmissionInfo("2", ".mp4", "1")
        ));
    }
}