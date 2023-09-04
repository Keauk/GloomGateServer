package com.example.gloomgate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    @Size(min = 4, max = 14, message = "Username must be between 4 and 14 characters long")
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}