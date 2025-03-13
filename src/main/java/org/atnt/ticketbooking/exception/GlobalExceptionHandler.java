package org.atnt.ticketbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookingException.class)
    public ResponseEntity<org.atnt.ticketbooking.exception.ErrorResponse> handleBookingException(BookingException ex) {
        org.atnt.ticketbooking.exception.ErrorResponse error = new org.atnt.ticketbooking.exception.ErrorResponse(ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<org.atnt.ticketbooking.exception.ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        org.atnt.ticketbooking.exception.ErrorResponse error = new org.atnt.ticketbooking.exception.ErrorResponse(ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<org.atnt.ticketbooking.exception.ErrorResponse> handleGenericException(Exception ex) {
        org.atnt.ticketbooking.exception.ErrorResponse error = new ErrorResponse("An unexpected error occurred: " + ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}