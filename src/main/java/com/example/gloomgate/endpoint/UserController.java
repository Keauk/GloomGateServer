package com.example.gloomgate.endpoint;

import com.example.gloomgate.entity.User;
import com.example.gloomgate.security.JwtUtil;
import com.example.gloomgate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = userService.registerNewUser(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {
        Optional<User> userOptional = userService.findByUsername(user.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Username not found.", "error", "Authentication Error"));
        }

        User loggedInUser = userOptional.get();
        if (!userService.checkPassword(loggedInUser, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid password.", "error", "Authentication Error"));
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(Map.of("message", "Authentication successful", "token", token));
    }
}