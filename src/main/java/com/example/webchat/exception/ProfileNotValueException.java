package com.example.webchat.exception;

import java.util.NoSuchElementException;

public class ProfileNotValueException extends NoSuchElementException {
    public ProfileNotValueException(String msg) {
        super(msg);
    }

    public ProfileNotValueException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
