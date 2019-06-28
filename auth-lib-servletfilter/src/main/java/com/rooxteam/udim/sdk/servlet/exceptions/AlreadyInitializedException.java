package com.rooxteam.udim.sdk.servlet.exceptions;

public class AlreadyInitializedException extends RuntimeException {
    public AlreadyInitializedException() {
        super("Object has already been initialized.");
    }
}
