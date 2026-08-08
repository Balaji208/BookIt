package com.bookit.backend.exception;

public class APIException extends RuntimeException{
    private static final long serialVersionId = 1L;

    public APIException(){}

    public APIException(String message) {
        super(message);
    }
}
