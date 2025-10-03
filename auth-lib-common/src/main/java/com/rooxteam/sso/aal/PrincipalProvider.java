package com.rooxteam.sso.aal;

public interface PrincipalProvider {

    Principal getPrincipal(String token);

}
