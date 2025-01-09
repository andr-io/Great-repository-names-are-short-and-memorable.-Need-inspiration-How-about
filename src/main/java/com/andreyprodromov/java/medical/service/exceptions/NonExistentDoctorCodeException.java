package com.andreyprodromov.java.medical.service.exceptions;

public class NonExistentDoctorCodeException extends RuntimeException {
    public NonExistentDoctorCodeException(String message) {
        super(message);
    }
}
