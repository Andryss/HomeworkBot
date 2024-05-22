package ru.andryss.homeworkbot.commands.handlers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract command handler implementing work with user state
 * @param <T> user state class
 */
public abstract class StateCommandHandler<T> extends AbstractCommandHandler {

    private final Map<Long, T> userIdToState = new ConcurrentHashMap<>();

    /**
     * Puts state for given user
     */
    protected void putUserState(long userId, T state) {
        userIdToState.put(userId, state);
    }

    /**
     * Returns state for given user
     */
    protected T getUserState(long userId) {
        return userIdToState.get(userId);
    }

    /**
     * Clears user state before exit
     */
    @Override
    protected void exitForUser(Long userId) {
        userIdToState.remove(userId);
        super.exitForUser(userId);
    }
}
