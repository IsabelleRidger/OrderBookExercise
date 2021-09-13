package com.example.orderbookexercise;

import java.util.Objects;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(Objects.requireNonNull(message, "message is null"));
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
