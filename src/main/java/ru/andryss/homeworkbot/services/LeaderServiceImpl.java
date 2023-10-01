package ru.andryss.homeworkbot.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeaderServiceImpl implements LeaderService {

    @Value("${group.leaders.telegram.usernames}")
    private String leadersTelegramUsernames;

    private final Set<String> leadersUsernames = new HashSet<>();

    @PostConstruct
    private void initLeadersIds() {
        leadersUsernames.addAll(Arrays.asList(leadersTelegramUsernames.split(",")));
    }

    @Override
    public boolean isLeader(String username) {
        return leadersUsernames.contains(username);
    }
}
