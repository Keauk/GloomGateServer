package com.example.gloomgate.exception;

public class PasswordWrongSizeException extends RuntimeException {
    public PasswordWrongSizeException(String message) {
        super(message);
    }
}
