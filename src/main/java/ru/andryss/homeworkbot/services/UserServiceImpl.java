package ru.andryss.homeworkbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.homeworkbot.entities.UserEntity;
import ru.andryss.homeworkbot.repositories.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public boolean userNameExists(String name) {
        return userRepository.existsByName(name);
    }

    @Override
    public void putUserName(Long id, String name) {
        UserEntity user = userRepository.findById(id).orElse(new UserEntity(id, name));
        user.setName(name);
        userRepository.save(user);
    }

    @Override
    public Optional<String> getUserName(Long id) {
        return userRepository.findById(id).map(UserEntity::getName);
    }
}
