package com.campus.nearby.exception;

public class ServerException extends RuntimeException {
    private final int code;
    private final String message;

    public ServerException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    // Getters
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}