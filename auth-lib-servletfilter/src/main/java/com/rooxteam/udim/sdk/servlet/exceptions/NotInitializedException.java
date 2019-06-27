package com.rooxteam.udim.sdk.servlet.exceptions;

public class NotInitializedException extends RuntimeException {
    public NotInitializedException() {
        super("Object has not been initialized.");
    }
}
