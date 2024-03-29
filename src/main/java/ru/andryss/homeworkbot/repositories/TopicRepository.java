package ru.andryss.homeworkbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.andryss.homeworkbot.entities.TopicEntity;

import java.util.List;

/**
 * Repository for working with {@link TopicEntity}
 */
@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, String> {

    /**
     * List all topic names
     *
     * @return list of topic names
     */
    @Query(value = "select name from topics", nativeQuery = true)
    List<String> findAllTopicsNames();
}
