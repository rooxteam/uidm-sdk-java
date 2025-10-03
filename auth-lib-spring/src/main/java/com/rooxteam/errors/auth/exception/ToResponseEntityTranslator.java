package com.rooxteam.errors.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Создает ResponseEntity, где телом является AuthApiException, а код выставляется из e.getHttpStatus()
 */
public class ToResponseEntityTranslator implements ErrorTranslator {


    public ToResponseEntityTranslator() {
    }

    @Override
    public ResponseEntity translate(AuthApiException e) {
        return new ResponseEntity(
                e,
                HttpStatus.valueOf(e.getCode())
        );
    }
}
