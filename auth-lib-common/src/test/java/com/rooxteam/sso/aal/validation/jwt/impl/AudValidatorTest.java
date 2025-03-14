package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
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
 * @since 15.07.2024
 */
public class AudValidatorTest {

    private final AudValidator validator = new AudValidator();

    @Test
    public void validate_positive_one_aud() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_aud");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config), null);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claimsBuilder.audience("test_aud");
        JWT jwt = new PlainJWT(claimsBuilder.build());
        ValidationResult result = validator.validate(jwt);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void validate_positive_several_auds() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_aud");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config), null);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claimsBuilder.audience(Arrays.asList("test_aud", "test_aud2"));
        JWT jwt = new PlainJWT(claimsBuilder.build());
        ValidationResult result = validator.validate(jwt);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void validate_positive_several_auds2() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_aud");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config), null);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claimsBuilder.audience(Arrays.asList("test_aud2", "test_aud"));
        JWT jwt = new PlainJWT(claimsBuilder.build());
        ValidationResult result = validator.validate(jwt);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void validate_negative_empty_aud() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_aud");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config), null);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claimsBuilder.audience(Collections.emptyList());
        JWT jwt = new PlainJWT(claimsBuilder.build());
        ValidationResult result = validator.validate(jwt);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ValidationResult.Reason.InvalidAudience, result.getReason());
    }

    @Test
    public void validate_negative_absent_aud() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_aud");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config), null);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        JWT jwt = new PlainJWT(claimsBuilder.build());
        ValidationResult result = validator.validate(jwt);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ValidationResult.Reason.InvalidAudience, result.getReason());
    }

    @Test
    public void validate_negative_null_aud() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_aud");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config), null);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claimsBuilder.audience((List) null);
        JWT jwt = new PlainJWT(claimsBuilder.build());
        ValidationResult result = validator.validate(jwt);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ValidationResult.Reason.InvalidAudience, result.getReason());
    }

    @Test
    public void validate_negative_aud_does_not_match() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_aud");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config), null);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claimsBuilder.audience("test_aud2");
        JWT jwt = new PlainJWT(claimsBuilder.build());
        ValidationResult result = validator.validate(jwt);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ValidationResult.Reason.InvalidAudience, result.getReason());
    }

    @Test
    public void validate_negative_client_id_not_configured() {
        Configuration config = new BaseConfiguration();
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config), null);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claimsBuilder.audience("test_aud");
        JWT jwt = new PlainJWT(claimsBuilder.build());
        ValidationResult result = validator.validate(jwt);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ValidationResult.Reason.InvalidAudience, result.getReason());
    }

    @Test
    public void validate_config_multiple_auds() {
        Configuration config = new BaseConfiguration();
        config.setProperty(ConfigKeys.CLIENT_ID, "test_client_id1");
        config.setProperty(ConfigKeys.ALLOWED_CLIENT_IDS, "test_client_id2, test_client_id3");
        validator.configure(ConfigurationBuilder.fromApacheCommonsConfiguration(config), null);

        // test positive
        Arrays.asList(Collections.singletonList("test_client_id1"),
                Collections.singletonList("test_client_id2"),
                Collections.singletonList("test_client_id3"),
                Arrays.asList("test_client_id1", "test_client_id2"),
                Arrays.asList("test_client_id2", "test_client_id4"),
                Arrays.asList("test_client_id1", "test_client_id4"))

                .forEach(audience -> {
                    JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
                    claimsBuilder.audience(audience);
                    JWT jwt = new PlainJWT(claimsBuilder.build());
                    ValidationResult result = validator.validate(jwt);
                    assertNotNull(result);
                    assertTrue(result.isSuccess());
                });

        // test negative
        Arrays.asList(Collections.singletonList("test_client_id4"),
                Collections.singletonList(""),
                Collections.emptyList(),
                null,
                Arrays.asList("test_client_id4", "test_client_id5"))

                .forEach(audience -> {
                    JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
                    claimsBuilder.audience((List) audience);
                    JWT jwt = new PlainJWT(claimsBuilder.build());
                    ValidationResult result = validator.validate(jwt);
                    assertNotNull(result);
                    assertFalse(result.isSuccess());
                    assertEquals(ValidationResult.Reason.InvalidAudience, result.getReason());
                });
    }

}