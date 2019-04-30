package com.rooxteam.errors.exception;

import org.springframework.http.ResponseEntity;

public interface ErrorTranlator {

    ResponseEntity translate(ApiException e);
}
