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
    @Query(value = "select name from submissions join topics on submissions.topic_id = topics.id where user_id = :userId", nativeQuery = true)
    List<String> listTopicsSubmittedBy(Long userId);
}
