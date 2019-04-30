package com.rooxteam.errors.exception;

import org.springframework.http.ResponseEntity;

/**
 * Создает ResponseEntity, где телом является ApiException, а код выставляется из e.getHttpStatus()
 */
public class ToResponseEntityTranslator implements ErrorTranlator {


    public ToResponseEntityTranslator() {
    }

    @Override
    public ResponseEntity translate(ApiException e) {
        return new ResponseEntity(
                e,
                e.getHttpStatus()
        );
    }
}
