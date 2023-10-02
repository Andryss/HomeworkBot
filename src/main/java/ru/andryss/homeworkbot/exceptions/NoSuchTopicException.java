package ru.andryss.homeworkbot.exceptions;

public class NoSuchTopicException extends RuntimeException {
    public NoSuchTopicException(String message) {
        super(message);
    }
}
