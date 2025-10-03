package com.rooxteam.errors.auth.exception;

import org.springframework.http.ResponseEntity;

public interface ErrorTranslator {

    ResponseEntity translate(AuthApiException e);
}
