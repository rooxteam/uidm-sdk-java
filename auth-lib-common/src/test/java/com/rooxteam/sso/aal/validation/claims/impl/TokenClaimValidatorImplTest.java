package com.rooxteam.sso.aal.validation.claims.impl;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import com.rooxteam.sso.aal.validation.claims.ClaimValidatorSPI;
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
public class TokenClaimValidatorImplTest {

    @Test
    public void init_validators_empty_no_config() {

        Map<String, Object> configMap = Collections.emptyMap();
        TokenClaimValidatorImpl sut = new TokenClaimValidatorImpl(ConfigurationBuilder.fromMap(configMap));

        List<Class<? extends ClaimValidatorSPI>> validators = extractValidatorClasses(sut);
        assertNotNull(validators);
        assertEquals(1, validators.size());

        assertTrue(validators.contains(ClientIdValidator.class));
    }

    @Test
    public void init_validators_with_empty_config() {

        Map<String, Object> configMap = new HashMap<String, Object>() {{
            put(ConfigKeys.CLAIM_VALIDATORS, "");
        }};

        TokenClaimValidatorImpl sut = new TokenClaimValidatorImpl(ConfigurationBuilder.fromMap(configMap));

        List<Class<? extends ClaimValidatorSPI>> validators = extractValidatorClasses(sut);
        assertNotNull(validators);
        assertEquals(0, validators.size());
    }

    @Test
    public void init_all_validators() {

        Map<String, Object> configMap = new HashMap<String, Object>() {{
            put(ConfigKeys.CLAIM_VALIDATORS, "com.rooxteam.sso.aal.validation.claims.impl.ClientIdValidator");
        }};

        TokenClaimValidatorImpl sut = new TokenClaimValidatorImpl(ConfigurationBuilder.fromMap(configMap));

        List<Class<? extends ClaimValidatorSPI>> validators = extractValidatorClasses(sut);
        assertNotNull(validators);
        assertEquals(1, validators.size());

        assertTrue(validators.contains(ClientIdValidator.class));
    }

    @SneakyThrows
    private List<Class<? extends ClaimValidatorSPI>> extractValidatorClasses(TokenClaimValidatorImpl validator) {
        Field field = validator.getClass().getDeclaredField("validators");
        field.setAccessible(true);
        List<ClaimValidatorSPI> list = (List<ClaimValidatorSPI>) field.get(validator);
        return list.stream().map(v -> v.getClass()).collect(Collectors.toList());
    }


}