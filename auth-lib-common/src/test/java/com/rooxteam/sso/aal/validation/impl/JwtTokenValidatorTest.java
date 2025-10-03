package com.rooxteam.sso.aal.validation.impl;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import com.rooxteam.sso.aal.validation.jwt.JwtValidatorSPI;
import com.rooxteam.sso.aal.validation.jwt.impl.AlgNoneValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.AlgValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.AudValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.EsSignatureValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.HsSignatureValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.IssueTimeValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.IssuerValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.RsSignatureValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.SignatureValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.SubNotEmptyValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.TimeIntervalValidator;
import lombok.SneakyThrows;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author sergey.syroezhkin
 * @since 18.07.2024
 */
public class JwtTokenValidatorTest {

    @Test
    public void init_validators_empty_no_config() {

        Map<String, Object> configMap = Collections.emptyMap();
        JwtTokenValidator sut = new JwtTokenValidator(ConfigurationBuilder.fromMap(configMap), null);

        List<Class<? extends JwtValidatorSPI>> validators = extractValidatorClasses(sut);
        assertNotNull(validators);
        assertEquals(3, validators.size());

        assertTrue(validators.contains(AlgNoneValidator.class));
        assertTrue(validators.contains(TimeIntervalValidator.class));
        assertTrue(validators.contains(AudValidator.class));
    }

    @Test
    public void init_validators_with_empty_config() {

        Map<String, Object> configMap = new HashMap<String, Object>() {{
            put(ConfigKeys.JWT_VALIDATORS, "");
        }};

        JwtTokenValidator sut = new JwtTokenValidator(ConfigurationBuilder.fromMap(configMap), null);

        List<Class<? extends JwtValidatorSPI>> validators = extractValidatorClasses(sut);
        assertNotNull(validators);
        assertEquals(2, validators.size());

        assertTrue(validators.contains(AlgNoneValidator.class));
        assertTrue(validators.contains(TimeIntervalValidator.class));
    }

    @Test
    public void init_all_validators() {

        Map<String, Object> configMap = new HashMap<String, Object>() {{
            put(ConfigKeys.JWT_VALIDATORS,
                    "com.rooxteam.sso.aal.validation.jwt.impl.AudValidator," +
                    "com.rooxteam.sso.aal.validation.jwt.impl.AlgValidator," +
                    "com.rooxteam.sso.aal.validation.jwt.impl.SignatureValidator," +
                    "com.rooxteam.sso.aal.validation.jwt.impl.EsSignatureValidator," +
                    "com.rooxteam.sso.aal.validation.jwt.impl.HsSignatureValidator," +
                    "com.rooxteam.sso.aal.validation.jwt.impl.RsSignatureValidator," +
                    "com.rooxteam.sso.aal.validation.jwt.impl.IssuerValidator," +
                    "com.rooxteam.sso.aal.validation.jwt.impl.IssueTimeValidator," +
                    "com.rooxteam.sso.aal.validation.jwt.impl.SubNotEmptyValidator"
            );
            put(ConfigKeys.JWT_VALIDATION_ALLOWED_ALGORITHMS, new String[] { "RS256" });
            put(ConfigKeys.JWKS_URL, "https://example.com/jwks");
            put(ConfigKeys.JWT_VALIDATION_HS_SHARED_SECRET, "shared key");
        }};

        JwtTokenValidator sut = new JwtTokenValidator(ConfigurationBuilder.fromMap(configMap), null);

        List<Class<? extends JwtValidatorSPI>> validators = extractValidatorClasses(sut);
        assertNotNull(validators);
        assertEquals(11, validators.size());

        assertTrue(validators.contains(AlgNoneValidator.class));
        assertTrue(validators.contains(TimeIntervalValidator.class));
        assertTrue(validators.contains(AudValidator.class));
        assertTrue(validators.contains(AlgValidator.class));
        assertTrue(validators.contains(SignatureValidator.class));
        assertTrue(validators.contains(EsSignatureValidator.class));
        assertTrue(validators.contains(HsSignatureValidator.class));
        assertTrue(validators.contains(RsSignatureValidator.class));
        assertTrue(validators.contains(IssuerValidator.class));
        assertTrue(validators.contains(IssueTimeValidator.class));
        assertTrue(validators.contains(SubNotEmptyValidator.class));
    }

    @Test
    public void init_validators_from_legacy_key() {

        Map<String, Object> configMap = new HashMap<String, Object>() {{
            put(JwtTokenValidator.JWT_VALIDATORS_LEGACY, "com.rooxteam.sso.aal.validation.jwt.impl.SubNotEmptyValidator");
        }};

        JwtTokenValidator sut = new JwtTokenValidator(ConfigurationBuilder.fromMap(configMap), null);

        List<Class<? extends JwtValidatorSPI>> validators = extractValidatorClasses(sut);
        assertNotNull(validators);
        assertEquals(3, validators.size());

        assertTrue(validators.contains(AlgNoneValidator.class));
        assertTrue(validators.contains(TimeIntervalValidator.class));
        assertTrue(validators.contains(SubNotEmptyValidator.class));
    }

    @Test
    public void init_validators_ignore_legacy_key() {

        Map<String, Object> configMap = new HashMap<String, Object>() {{
            put(ConfigKeys.JWT_VALIDATORS, "com.rooxteam.sso.aal.validation.jwt.impl.IssuerValidator");
            put(JwtTokenValidator.JWT_VALIDATORS_LEGACY, "com.rooxteam.sso.aal.validation.jwt.impl.SubNotEmptyValidator");
        }};

        JwtTokenValidator sut = new JwtTokenValidator(ConfigurationBuilder.fromMap(configMap), null);

        List<Class<? extends JwtValidatorSPI>> validators = extractValidatorClasses(sut);
        assertNotNull(validators);
        assertEquals(3, validators.size());

        assertTrue(validators.contains(AlgNoneValidator.class));
        assertTrue(validators.contains(TimeIntervalValidator.class));
        assertTrue(validators.contains(IssuerValidator.class));
    }

    @SneakyThrows
    private List<Class<? extends JwtValidatorSPI>> extractValidatorClasses(JwtTokenValidator validator) {
        Field field = validator.getClass().getDeclaredField("validators");
        field.setAccessible(true);
        List<JwtValidatorSPI> list = (List<JwtValidatorSPI>) field.get(validator);
        return list.stream().map(v -> v.getClass()).collect(Collectors.toList());
    }

}