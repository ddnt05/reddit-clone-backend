package com.example.redditclone.exception;

public class MissingParameterException extends Exception{
    private final String status = "error";

    public MissingParameterException(String message) {
        super(message);
    }
}
