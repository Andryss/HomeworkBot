package ru.andryss.homeworkbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andryss.homeworkbot.entities.UserEntity;

/**
 * Repository for working with {@link UserEntity}
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * Checks whether user exists by username
     *
     * @param name username to check
     * @return true - user exists
     */
    boolean existsByName(String name);
}
