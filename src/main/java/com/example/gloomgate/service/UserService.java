package com.example.gloomgate.service;

import com.example.gloomgate.entity.User;
import com.example.gloomgate.exception.PasswordWrongSizeException;
import com.example.gloomgate.exception.UsernameAlreadyExistsException;
import com.example.gloomgate.repository.UserRepository;
import com.example.gloomgate.util.PasswordUtility;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final Validator validator;

    public UserService(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public User registerNewUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already exists!");
        }

        if (rawPassword.length() < 8 || rawPassword.length() > 50) {
            throw new PasswordWrongSizeException("Password must be between 8 and 50 characters long");
        }

        com.example.gloomgate.entity.User user = new com.example.gloomgate.entity.User();

        user.setUsername(username);
        user.setPassword(PasswordUtility.hashPassword(rawPassword));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean checkPassword(User user, String rawPassword) {
        return PasswordUtility.checkPassword(rawPassword, user.getPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        User user = userOptional.get();

        // Since roles aren't used, you can provide an empty list of authorities.
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}