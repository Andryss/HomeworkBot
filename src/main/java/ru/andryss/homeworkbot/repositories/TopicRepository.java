package ru.andryss.homeworkbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andryss.homeworkbot.entities.TopicEntity;

@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
}
