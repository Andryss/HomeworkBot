package ru.andryss.homeworkbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.andryss.homeworkbot.entities.TopicEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
    boolean existsByName(String name);
    Optional<TopicEntity> findByName(String name);
    @Query(value = "select name from topics", nativeQuery = true)
    List<String> findAllTopicsNames();
}
