package org.saga.example.payment.exceptions;

import org.saga.example.shared.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value= UserNotFoundException.class)
    public @ResponseBody ErrorResponse handleException(Exception ex){
        return new ErrorResponse(HttpStatus.NO_CONTENT.value(), ex.getMessage());
    }
}
