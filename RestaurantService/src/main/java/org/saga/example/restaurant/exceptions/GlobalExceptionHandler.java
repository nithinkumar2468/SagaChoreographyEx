package org.saga.example.restaurant.exceptions;

import org.saga.example.shared.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = HotelNotFoundException.class)
    public @ResponseBody ErrorResponse exceptionHandler(HotelNotFoundException ex){
        return new ErrorResponse(HttpStatus.NO_CONTENT.value(), ex.getMessage());
    }

    @ExceptionHandler(value= ProductNotFoundException.class)
    public @ResponseBody ErrorResponse productNotFoundHandler(ProductNotFoundException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage());
    }

    @ExceptionHandler(value= OrderNotFoundException.class)
    public @ResponseBody ErrorResponse orderNotFoundException(OrderNotFoundException ex){
        return new ErrorResponse(HttpStatus.NO_CONTENT.value(), ex.getMessage());
    }

}
