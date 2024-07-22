package org.saga.example.restaurant.exceptions;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException() {
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
