package ru.andryss.homeworkbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.andryss.homeworkbot.entities.SubmissionEntity;

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
        from submissions s join topics t on s.topic_name = t.name
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
        select s.file_id, s.extension, s.upload_datetime, s.topic_name, s.user_id
        from submissions s join topics t on s.topic_name = t.name
        where t.name = :topic
        """, nativeQuery = true)
    List<SubmissionEntity> findAllByTopic(String topic);
}
