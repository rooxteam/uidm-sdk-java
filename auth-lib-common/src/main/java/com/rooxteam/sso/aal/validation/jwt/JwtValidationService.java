package com.rooxteam.sso.aal.validation.jwt;


import com.nimbusds.jwt.JWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validate access token by different means configured in configuration
 */
public interface JwtValidationService {


    /**
     * feature logger
     */
    Logger LOG = LoggerFactory.getLogger(JwtValidationService.class.getName());

    ValidationResult validate(JWT jwt);
}
