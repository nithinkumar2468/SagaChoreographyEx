package org.saga.example.order.exceptions;

import org.saga.example.shared.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value=HotelInactiveException.class)
    public @ResponseBody ErrorResponse handleException(HotelInactiveException ex){
        return new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
    }
    @ExceptionHandler(value= OrderNotFoundException.class)
    public @ResponseBody ErrorResponse orderNotFoundHandler(OrderNotFoundException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
    @ExceptionHandler(value = AwsSqsException.class)
    public @ResponseBody ErrorResponse handleAwsException(AwsSqsException ex){
        return new ErrorResponse(HttpStatus.NO_CONTENT.value(), ex.getMessage());
    }
}
