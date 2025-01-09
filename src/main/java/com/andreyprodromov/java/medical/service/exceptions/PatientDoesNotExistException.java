package com.andreyprodromov.java.medical.service.exceptions;

public class PatientDoesNotExistException extends RuntimeException {
    public PatientDoesNotExistException(String message) {
        super(message);
    }
}
