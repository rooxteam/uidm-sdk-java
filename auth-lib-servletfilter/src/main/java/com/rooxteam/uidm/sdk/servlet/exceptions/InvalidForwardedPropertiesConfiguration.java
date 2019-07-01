package com.rooxteam.uidm.sdk.servlet.exceptions;

public class InvalidForwardedPropertiesConfiguration extends RuntimeException {
    public InvalidForwardedPropertiesConfiguration() {
        super("Configuration is not valid. Array of the forwarded token claims should of the same size as array of header names and array of attribute name.");    }
}
