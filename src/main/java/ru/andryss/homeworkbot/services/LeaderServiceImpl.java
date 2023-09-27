package ru.andryss.homeworkbot.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.andryss.homeworkbot.repositories.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeaderServiceImpl implements LeaderService {

    private final UserRepository userRepository;

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
        return userRepository.existsById(userId) && leadersTelegramIds.contains(userId);
    }
}
