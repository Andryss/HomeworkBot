package ru.andryss.homeworkbot.services;

import jakarta.annotation.Nullable;

public interface UserService {
    boolean userNameExists(String name);
    void putUserName(Long id, String name);
    @Nullable
    String getUserName(Long id);
}
