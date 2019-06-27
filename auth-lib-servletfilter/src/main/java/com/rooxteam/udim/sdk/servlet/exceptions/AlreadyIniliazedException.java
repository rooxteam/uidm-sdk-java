package com.rooxteam.udim.sdk.servlet.exceptions;

public class AlreadyIniliazedException extends RuntimeException {
    public AlreadyIniliazedException() {
        super("Object has already been initialized.");
    }
}
