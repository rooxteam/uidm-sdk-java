package com.rooxteam.sso.aal.validation.impl;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.AccessTokenValidator;
import com.rooxteam.sso.aal.validation.jwt.JwtValidatorSPI;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import com.rooxteam.sso.aal.validation.jwt.impl.AlgNoneValidator;
import com.rooxteam.sso.aal.validation.jwt.impl.TimeIntervalValidator;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.rooxteam.sso.aal.AalLogger.LOG;

public class JwtTokenValidator implements AccessTokenValidator {

    private final List<JwtValidatorSPI> validators;


    public JwtTokenValidator(Configuration configuration, CloseableHttpClient httpClient) {
        this.validators = new ArrayList<>();

        // these cannot be disabled
        this.validators.add(new AlgNoneValidator());
        this.validators.add(new TimeIntervalValidator());

        List validatorClassNames = configuration.getList(ConfigKeys.JWT_VALIDATORS);
        if (validatorClassNames != null) {
            for (Object validatorClassName : validatorClassNames) {
                try {
                    JwtValidatorSPI validator = (JwtValidatorSPI) Class.forName(validatorClassName.toString()).newInstance();
                    validators.add(validator);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Failed to initialize JWT validator", e);
                }
            }
        }
        this.validators.forEach(jwtValidator -> jwtValidator.configure(configuration, httpClient));
    }

    @Override
    public Principal validate(HttpServletRequest request, String accessToken) {
        LOG.debugv("validate {0}", accessToken);

        JWT jwt;
        try {
            jwt = JWTParser.parse(accessToken);
        } catch (ParseException e) {
            LOG.warnv("JWT has not valid structure", e);
            return null;
        }

        ValidationResult validationResult = runValidators(validators, jwt);

        if (validationResult.isSuccess()) {
            LOG.debugv("JWT validated");
            JWTClaimsSet claimsSet = null;
            try {
                claimsSet = jwt.getJWTClaimsSet();
            } catch (ParseException e) {
                LOG.warnv("JWT has not valid structure", e);
                return null;
            }

            LOG.debugv("Claims: {0}", claimsSet);

            Calendar exp = Calendar.getInstance();
            exp.setTime(claimsSet.getExpirationTime());
            return new PrincipalImpl(accessToken, claimsSet.getClaims(), exp);
        } else {
            LOG.warnv("JWT not valid {0}", validationResult);
            return null;
        }
    }


    private ValidationResult runValidators(List<JwtValidatorSPI> validators, JWT accessToken) {
        if (validators == null || validators.isEmpty()) {
            LOG.debug("no validators in list");
            return ValidationResult.fail(ValidationResult.Reason.NoValidatorsConfigured);
        }
        for (JwtValidatorSPI validator : validators) {
            LOG.debugv("Running validator {0}", validator);
            ValidationResult result = validator.validate(accessToken);
            if (!result.isSuccess()) {
                LOG.debugv("Validator {0} failed validation with result {1}", validator, result);
                return result;
            }
        }
        LOG.debug("runValidators all validators finished with success");
        return ValidationResult.success();
    }
}
