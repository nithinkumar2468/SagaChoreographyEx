package org.saga.example.payment.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
    }
    public UserNotFoundException(String message) {
        super(message);
    }
}
