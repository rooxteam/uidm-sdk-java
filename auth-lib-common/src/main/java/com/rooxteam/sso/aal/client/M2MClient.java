package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Closeable;
import java.util.Map;

/**
 * This interface is not stable yet and may be changed in the future
 */
public interface M2MClient extends Closeable {
    JsonNode authenticate(String clientId, Map<String, String> args) throws Exception;
}
