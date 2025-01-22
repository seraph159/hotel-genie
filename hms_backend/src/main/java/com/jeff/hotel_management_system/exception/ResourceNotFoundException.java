package com.jeff.hotel_management_system.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {

        super(message);
    }
}