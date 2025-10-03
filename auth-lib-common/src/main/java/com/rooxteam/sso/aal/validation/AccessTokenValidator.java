package com.rooxteam.sso.aal.validation;

import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;

public interface AccessTokenValidator {

    ValidationResult validate(String token);

    ValidationResult validate(JWT jwtToken);
}
