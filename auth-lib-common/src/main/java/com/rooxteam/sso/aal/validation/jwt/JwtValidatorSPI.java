package com.rooxteam.sso.aal.validation.jwt;

import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.configuration.Configuration;
import org.apache.http.impl.client.CloseableHttpClient;


/**
 * SPI to implement some kind of validation
 */
public interface JwtValidatorSPI {

    void configure(Configuration configuration, CloseableHttpClient httpClient);

    ValidationResult validate(JWT jwt);
}
