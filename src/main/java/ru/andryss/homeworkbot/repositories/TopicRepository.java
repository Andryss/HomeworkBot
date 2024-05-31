package ru.andryss.homeworkbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.andryss.homeworkbot.entities.TopicEntity;

import java.util.List;

/**
 * Repository for working with {@link TopicEntity}
 */
@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, String> {

    /**
     * Check whether topic exist
     *
     * @param name topic name to search
     * @return true - topic exist
     */
    boolean existsByName(String name);

    /**
     * Remove topic by name
     *
     * @param name topic name to remove
     */
    @Transactional
    void removeByName(String name);

    /**
     * List all topic names
     *
     * @return list of topic names
     */
    @Query(value = "select name from topics", nativeQuery = true)
    List<String> findAllTopicsNames();
}
