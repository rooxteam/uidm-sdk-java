package com.rooxteam.sso.aal.validation.claims.impl;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import com.rooxteam.sso.aal.validation.claims.ValidationResult;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author sergey.syroezhkin
 * @since 16.07.2024
 */
public class ClientIdValidatorTest {

    private final ClientIdValidator validator = new ClientIdValidator();

    @Test
    public void validate_positive() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_client_id");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config));

        Map<String, Object> claims = new HashMap<>();
        claims.put("client_id", "test_client_id");
        ValidationResult result = validator.validate(claims);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void validate_negative_empty_client_id() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_client_id");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config));

        Map<String, Object> claims = new HashMap<>();
        claims.put("client_id", "");
        ValidationResult result = validator.validate(claims);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ValidationResult.Reason.InvalidClientId, result.getReason());
    }

    @Test
    public void validate_negative_absent_client_id() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_client_id");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config));

        ValidationResult result = validator.validate(Collections.emptyMap());
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ValidationResult.Reason.InvalidClientId, result.getReason());
    }

    @Test
    public void validate_negative_null_client_id() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_client_id");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config));

        Map<String, Object> claims = new HashMap<>();
        claims.put("client_id", null);
        ValidationResult result = validator.validate(claims);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ValidationResult.Reason.InvalidClientId, result.getReason());
    }

    @Test
    public void validate_negative_client_id_does_not_match() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_client_id");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config));

        Map<String, Object> claims = new HashMap<>();
        claims.put("client_id", "test_client_id2");
        ValidationResult result = validator.validate(claims);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ValidationResult.Reason.InvalidClientId, result.getReason());
    }

    @Test
    public void validate_negative_client_id_not_configured() {
        Configuration config = new BaseConfiguration();
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config));

        Map<String, Object> claims = new HashMap<>();
        claims.put("client_id", "test_client_id");
        ValidationResult result = validator.validate(claims);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ValidationResult.Reason.InvalidClientId, result.getReason());
    }

    @Test
    public void validate_multiple_client_ids() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_client_id1");
        config.setProperty(ConfigKeys.ALLOWED_CLIENT_IDS, "test_client_id2, test_client_id3");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config));

        // test positive
        Arrays.asList("test_client_id1", "test_client_id2", "test_client_id3").forEach(clientId -> {
            Map<String, Object> claims = new HashMap<>();
            claims.put("client_id", clientId);
            ValidationResult result = validator.validate(claims);
            assertNotNull(result);
            assertTrue(result.isSuccess());
        });

        // test negative
        Arrays.asList("test_client_id4", "", null).forEach(clientId -> {
            Map<String, Object> claims = new HashMap<>();
            claims.put("client_id", clientId);
            ValidationResult result = validator.validate(claims);
            assertNotNull(result);
            assertFalse(result.isSuccess());
            assertEquals(ValidationResult.Reason.InvalidClientId, result.getReason());
        });
    }

}