package ru.andryss.homeworkbot.services;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
class LeaderServiceTest {

    // @see {group.leaders.telegram.usernames} property

    @Autowired
    LeaderService leaderService;

    @CsvSource({
            "1",
            "2",
            "3"
    })
    @ParameterizedTest
    void isLeader_userIsLeader_returnTrue(String leaderUsername) {
        boolean isLeader = leaderService.isLeader(leaderUsername);

        assertThat(isLeader, is(true));
    }

    @CsvSource({
            "",
            "0",
            "4",
            "non existing"
    })
    @ParameterizedTest
    void isLeader_userIsNotLeader_returnFalse(String nonLeaderUsername) {
        boolean isLeader = leaderService.isLeader(nonLeaderUsername);

        assertThat(isLeader, is(false));
    }

}