package ru.andryss.homeworkbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.andryss.homeworkbot.entities.UserEntity;
import ru.andryss.homeworkbot.repositories.UserRepository;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void putUserName(Long id, String name) {
        UserEntity user = userRepository.findById(id).orElse(new UserEntity(id, name));
        user.setName(name);
        userRepository.save(user);
    }

    @Override
    public String getUserName(Long id) {
        return userRepository.findById(id).map(UserEntity::getName).orElse(null);
    }
}
