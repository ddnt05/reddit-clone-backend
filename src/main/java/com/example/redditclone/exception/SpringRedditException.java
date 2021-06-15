package com.example.redditclone.exception;

public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
