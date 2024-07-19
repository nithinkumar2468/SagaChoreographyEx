package org.saga.example.order.exceptions;

public class HotelInactiveException extends RuntimeException{

    public HotelInactiveException() {
        super();
    }
    public HotelInactiveException(String message) {
        super(message);
    }

}
