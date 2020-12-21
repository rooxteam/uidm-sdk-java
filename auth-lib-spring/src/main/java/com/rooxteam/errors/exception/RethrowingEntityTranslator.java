package com.rooxteam.errors.exception;

import org.springframework.http.ResponseEntity;

/**
 * Выбрасывает исключение заново. Годится для тех приложений, которым удобнее иметь единообразный способ
 * обработки исключений.
 */
public class RethrowingEntityTranslator implements ErrorTranslator {


    public RethrowingEntityTranslator() {
    }

    @Override
    public ResponseEntity translate(ApiException e) {
        throw e;
    }
}
