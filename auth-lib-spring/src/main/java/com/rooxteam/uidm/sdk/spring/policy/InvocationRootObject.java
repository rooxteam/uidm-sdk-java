package com.rooxteam.uidm.sdk.spring.policy;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.uidm.sdk.spring.authorization.AalResourceValidation;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

/**
 * @author sergey.syroezhkin
 * @since 14.01.2021
 */
abstract class InvocationRootObject implements SecurityExpressionOperations, AalResourceValidation {
    private final Authentication authentication;
    private final Principal principal;

    InvocationRootObject(Authentication authentication, Principal principal) {
        this.authentication = authentication;
        this.principal = principal;
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public boolean hasAuthority(String authority) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean hasAnyAuthority(String... authorities) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean hasRole(String role) {
        if (principal == null) {
            return false;
        }
        //noinspection unchecked
        List<String> roles = (List<String>) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "roles");
        return roles != null && roles.contains(role);
    }

    @Override
    public final boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean permitAll() {
        return false;
    }

    @Override
    public boolean denyAll() {
        return false;
    }

    @Override
    public boolean isAnonymous() {
        return !isAuthenticated();
    }

    public boolean isAuthenticated() {
        return principal != null && !principal.isAnonymous();
    }

    @Override
    public boolean isRememberMe() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isFullyAuthenticated() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean hasPermission(Object target, Object permission) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean hasPermission(Object targetId, String targetType, Object permission) {
        throw new IllegalStateException("not implemented");
    }

    public boolean isResourceAllowed(String resourceName, String actionName) {
        return isResourceAllowed(resourceName, actionName, null);
    }

    public boolean isResourceAllowed(String resourceName, String actionName, Map<String, ?> envParams) {
        return isAllowed(resourceName, actionName, envParams);
    }

    @Override
    public boolean isAllowed(String resource, String operation) {
        return isAllowed(resource, operation, null);
    }

    public abstract boolean isAllowed(String resource, String operation, Map<String, ?> envParameters);

    @Override
    public String postprocess(String resource, String operation, Map<String, ?> envParameters, String response) {
        throw new IllegalStateException("not implemented");
    }
}
