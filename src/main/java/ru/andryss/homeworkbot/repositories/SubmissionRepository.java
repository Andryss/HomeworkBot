package ru.andryss.homeworkbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.andryss.homeworkbot.entities.SubmissionEntity;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, String> {
    @Query(value = """
    select name from topics where name not in 
        (select name from submissions join topics on submissions.topic_id = topics.id where user_id = :userId)
    """, nativeQuery = true)
    List<String> listTopicsNotSubmittedBy(Long userId);
}
