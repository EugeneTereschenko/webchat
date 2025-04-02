package com.example.webchat.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserBlockedException extends UsernameNotFoundException {
    public UserBlockedException(String msg) {
        super(msg);
    }
}
