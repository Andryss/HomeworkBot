package ru.andryss.homeworkbot.services;

public interface UserService {
    void putUserName(Long id, String name);
    String getUserName(Long id);
}
