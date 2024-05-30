package ru.andryss.homeworkbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.andryss.homeworkbot.entities.SubmissionEntity;
import ru.andryss.homeworkbot.entities.UserSubmissionInfo;

import java.util.List;

/**
 * Repository for working with {@link SubmissionEntity}
 */
@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, String> {
    /**
     * List topics submitted by given user
     *
     * @param userId user id to search
     * @return list of submitted topics
     */
    @Query(value = """
        select t.name
        from submissions s join topics t on s.topic_id = t.id
        where s.user_id = :userId
        """, nativeQuery = true)
    List<String> listTopicsSubmittedBy(Long userId);

    /**
     * Finds all submission on given topic
     *
     * @param topic topic name to search
     * @return submissions
     */
    @Query(value = """
        select new ru.andryss.homeworkbot.entities.UserSubmissionInfo(t.name, s.fileId, s.extension, u.name)
        from SubmissionEntity s join TopicEntity t on s.topicId = t.id join UserEntity u on s.userId = u.id
        where t.name = :topic
        """)
    List<UserSubmissionInfo> findAllByTopic(String topic);

    /**
     * Removes all submission on given topic
     *
     * @param topic topic name to search
     */
    @Modifying
    @Transactional
    @Query(value = """
        delete from submissions where topic_id = (select id from topics where name = :topic)
        """, nativeQuery = true)
    void deleteAllByTopic(String topic);

    /**
     * Finds all submissions
     *
     * @return submissions
     */
    @Query("""
        select new ru.andryss.homeworkbot.entities.UserSubmissionInfo(t.name, s.fileId, s.extension, u.name)
        from SubmissionEntity s join TopicEntity t on s.topicId = t.id join UserEntity u on s.userId = u.id
        """)
    List<UserSubmissionInfo> findAllSubmissions();
}
