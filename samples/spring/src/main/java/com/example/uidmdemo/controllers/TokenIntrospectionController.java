package com.example.uidmdemo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens")
public class TokenIntrospectionController {

    @GetMapping(value = "/@current", produces = "application/json")
    public Authentication get(Authentication authentication) {
        return authentication;
    }
}
