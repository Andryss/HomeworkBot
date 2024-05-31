package ru.andryss.homeworkbot.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.andryss.homeworkbot.repositories.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void cleanRepository() {
        userRepository.deleteAll();
    }

    @Test
    void getUserName_userNotExists_returnEmpty() {
        Optional<String> user = userService.getUserName(1L);

        assertThat(user.isEmpty(), is(true));
    }

    @Test
    void getUserName_userExists_returnUser() {
        userService.putUserName(1L, "1");

        Optional<String> userOptional = userService.getUserName(1L);

        assertThat(userOptional.isPresent(), is(true));

        String user = userOptional.get();
        assertThat(user, is("1"));
    }

    @Test
    void userNameExists_nameNotExists_returnFalse() {
        boolean isExist = userService.userNameExists("non existing");

        assertThat(isExist, is(false));
    }

    @Test
    void userNameExists_nameExists_returnTrue() {
        userService.putUserName(1L, "1");

        boolean isExist = userService.userNameExists("1");

        assertThat(isExist, is(true));
    }

}