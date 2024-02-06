package ru.andryss.homeworkbot.services;

/**
 * Service to work with group leaders
 */
public interface LeaderService {
    /**
     * Checks whether user is leader or not
     *
     * @param username user to check
     * @return true - user is leader
     */
    boolean isLeader(String username);
}
