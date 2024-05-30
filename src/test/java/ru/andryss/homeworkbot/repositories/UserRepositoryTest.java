package ru.andryss.homeworkbot.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.andryss.homeworkbot.entities.UserEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void cleanRepository() {
        userRepository.deleteAll();
    }

    @Test
    void existByName_repositoryEmpty_returnFalse() {
        boolean isExist = userRepository.existsByName("non existing");

        assertThat(isExist, is(false));
    }

    @Test
    void existByName_nameExist_returnTrue() {
        userRepository.save(new UserEntity(1L, "user"));

        boolean isExist = userRepository.existsByName("user");

        assertThat(isExist, is(true));
    }
}