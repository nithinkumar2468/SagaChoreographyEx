package org.saga.example.order.exceptions;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException() {
    }
    public OrderNotFoundException(String message) {
        super(message);
    }
}
