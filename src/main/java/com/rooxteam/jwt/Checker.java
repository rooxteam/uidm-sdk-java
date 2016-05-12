package com.rooxteam.jwt;

import com.nimbusds.jwt.JWT;

import java.security.SignatureException;
import java.text.ParseException;

public interface Checker {

    void check(JWT jwt) throws SignatureException, ParseException;
}
