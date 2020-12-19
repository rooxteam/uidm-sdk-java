package com.rooxteam.errors.exception;

import org.springframework.http.ResponseEntity;

public interface ErrorTranslator {

    ResponseEntity translate(ApiException e);
}
