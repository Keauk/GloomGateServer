package com.example.gloomgate.service;

import com.example.gloomgate.entity.User;
import com.example.gloomgate.exception.UsernameAlreadyExistsException;
import com.example.gloomgate.repository.UserRepository;
import com.example.gloomgate.util.PasswordUtility;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerNewUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already exists!");
        }

        User user = new User();

        user.setUsername(username);
        user.setPassword(PasswordUtility.hashPassword(rawPassword));

        return userRepository.save(user);
    }

    public boolean checkPassword(User user, String rawPassword) {
        return PasswordUtility.checkPassword(rawPassword, user.getPassword());
    }
}