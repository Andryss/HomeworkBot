package ru.andryss.homeworkbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.andryss.homeworkbot.entities.TopicEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repository for working with {@link TopicEntity}
 */
@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
    /**
     * Checks whether topic name exists or not
     *
     * @param name topic name to check
     * @return true - topic exists
     */
    boolean existsByName(String name);

    /**
     * Find topic by given name
     *
     * @param name topic name to find
     * @return topic entity
     */
    Optional<TopicEntity> findByName(String name);

    /**
     * List all topic names
     *
     * @return list of topic names
     */
    @Query(value = "select name from topics", nativeQuery = true)
    List<String> findAllTopicsNames();
}
