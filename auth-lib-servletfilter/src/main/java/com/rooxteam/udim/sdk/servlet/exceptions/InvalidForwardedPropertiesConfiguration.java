package com.rooxteam.udim.sdk.servlet.exceptions;

public class InvalidForwardedPropertiesConfiguration extends RuntimeException {
    public InvalidForwardedPropertiesConfiguration() {
        super("Configuration is not valid. Some properties are missing corresponding headers or some headers are missing corresponding properties.");    }
}
