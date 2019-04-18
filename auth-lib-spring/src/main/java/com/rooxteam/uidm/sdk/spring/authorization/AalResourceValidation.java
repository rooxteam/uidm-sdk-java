package com.rooxteam.uidm.sdk.spring.authorization;

import java.util.Map;

/**
 *
 */
public interface AalResourceValidation {

    boolean isAllowed(String resource, String operation);

    boolean isAllowed(String resource, String operation, Map<String, ?> envParameters);
}
