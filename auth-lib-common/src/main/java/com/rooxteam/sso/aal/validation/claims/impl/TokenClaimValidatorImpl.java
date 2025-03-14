package com.rooxteam.sso.aal.validation.claims.impl;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.utils.StringUtils;
import com.rooxteam.sso.aal.validation.claims.ClaimValidatorSPI;
import com.rooxteam.sso.aal.validation.claims.TokenClaimValidator;
import com.rooxteam.sso.aal.validation.claims.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * @author sergey.syroezhkin
 * @since 15.07.2024
 */
public class TokenClaimValidatorImpl implements TokenClaimValidator {

    private final List<ClaimValidatorSPI> validators;

    public TokenClaimValidatorImpl(Configuration configuration) {
        this.validators = new ArrayList<>();

        String classNamesProp = configuration.getString(ConfigKeys.CLAIM_VALIDATORS, ConfigKeys.CLAIM_VALIDATORS_DEFAULT);
        List<String> validatorClassNames = Stream.of(classNamesProp.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        for (Object validatorClassName : validatorClassNames) {
            try {
                ClaimValidatorSPI validator = (ClaimValidatorSPI) Class.forName(validatorClassName.toString()).newInstance();
                validators.add(validator);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to initialize JWT validator", e);
            }
        }
        this.validators.forEach(validator -> validator.configure(configuration));
    }

    @Override
    public ValidationResult validate(Map<String, Object> claims) {
        for (ClaimValidatorSPI validator : validators) {
            LOG.debugv("Running validator {0}", validator);
            ValidationResult result = validator.validate(claims);
            if (!result.isSuccess()) {
                LOG.infoFailedValidator(validator, result);
                return result;
            }
        }
        LOG.debug("all validators finished with success");
        return ValidationResult.success();
    }
}
