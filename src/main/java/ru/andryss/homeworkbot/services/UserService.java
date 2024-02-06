package ru.andryss.homeworkbot.services;

import jakarta.annotation.Nullable;

import java.util.Optional;

/**
 * Service to work with users
 */
public interface UserService {
    /**
     * Checks whether user exist or not
     *
     * @param name username to check
     * @return true - user exists
     */
    boolean userNameExists(String name);

    /**
     * Associate username with telegram id
     *
     * @param id user telegram id
     * @param name username to associate
     */
    void putUserName(Long id, String name);

    /**
     * Get username by telegram id
     *
     * @param id id to search
     * @return username
     */
    Optional<String> getUserName(Long id);
}
