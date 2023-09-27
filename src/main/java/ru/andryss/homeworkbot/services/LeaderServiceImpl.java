package ru.andryss.homeworkbot.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Set;

public class LeaderServiceImpl implements LeaderService {

    @Value("${group.leaders.telegram.ids}")
    private String leadersTelegramIdsString;

    private final Set<Long> leadersTelegramIds = new HashSet<>();

    @PostConstruct
    private void initLeadersIds() {
        String[] splitLeadersIds = leadersTelegramIdsString.split(",");
        for (String leaderId : splitLeadersIds) {
            leadersTelegramIds.add(Long.parseLong(leaderId));
        }
    }

    @Override
    public boolean isLeader(Long userId) {
        return leadersTelegramIds.contains(userId);
    }
}
