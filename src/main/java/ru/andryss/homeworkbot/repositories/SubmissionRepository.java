package ru.andryss.homeworkbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andryss.homeworkbot.entities.SubmissionEntity;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, String> {
}
