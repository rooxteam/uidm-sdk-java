package com.rooxteam.sso.aal;

import javax.servlet.http.HttpServletRequest;

public interface PrincipalProvider {

    Principal getPrincipal(HttpServletRequest request, String token);

}
