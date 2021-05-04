package com.auth.testlogin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * Applicable on all other controllers
 */
@ControllerAdvice
@RestController
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ApiException apiException =
                new ApiException(new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(apiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(WrongUserCredentialsException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(WrongUserCredentialsException ex, WebRequest request) {
        ex.printStackTrace();
        ApiException apiException =
                new ApiException(new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenNotValidException.class)
    public final ResponseEntity<Object> handleTokenNotValidException(TokenNotValidException ex, WebRequest request) {
        ex.printStackTrace();
        ApiException apiException =
                new ApiException(new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(apiException, HttpStatus.FORBIDDEN);
    }

}
