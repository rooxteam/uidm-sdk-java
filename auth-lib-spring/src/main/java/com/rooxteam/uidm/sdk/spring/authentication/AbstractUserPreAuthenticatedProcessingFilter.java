package com.rooxteam.uidm.sdk.spring.authentication;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Base class for processing filters that handle pre-authenticated authentication requests, where it is assumed
 * that the system principal has already been authenticated by an external system.
 * <p/>
 */
public abstract class AbstractUserPreAuthenticatedProcessingFilter extends GenericFilterBean implements
        ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher = null;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource
            = new WebAuthenticationDetailsSource();
    private boolean continueFilterChainOnUnsuccessfulAuthentication = true;
    private boolean checkForPrincipalChanges;
    private boolean invalidateSessionOnPrincipalChange = true;


    /**
     * Check whether all required properties have been set.
     */
    @Override
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        } catch (ServletException e) {
            // convert to RuntimeException for passivity on afterPropertiesSet signature
            throw new RuntimeException(e);
        }
    }

    /**
     * Try to authenticate a pre-authenticated user with Spring Security if the user has not yet been authenticated.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug("Checking secure context token: " + SecurityContextHolder.getContext().getAuthentication());
        }

        if (requiresUserAuthentication((HttpServletRequest) request)) {
            doAuthenticate((HttpServletRequest) request, (HttpServletResponse) response);
        }

        chain.doFilter(request, response);
    }

    /**
     * Do the actual authentication for a pre-authenticated user.
     */
    private void doAuthenticate(HttpServletRequest request, HttpServletResponse response) {

        AuthenticationState principal = (AuthenticationState) getPreAuthenticatedUserPrincipal(request);

        if (principal == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No pre-authenticated principal found in request");
            }
            unsuccessfulUserAuthentication(request, response, null);

            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("preAuthenticatedPrincipal = " + principal + ", trying to authenticate");
        }

        successfulUserAuthentication(request, response, principal);
    }

    private boolean requiresUserAuthentication(HttpServletRequest request) {
        AuthenticationState authenticationState = getAuthenticationState();

        if (authenticationState == null || authenticationState.getUserAuthentication() == null) {
            return true;
        }

        if (!checkForPrincipalChanges) {
            return false;
        }

        Object principal = getPreAuthenticatedUserPrincipal(request);

        if (authenticationState.getUserAuthentication().equals(principal)) {
            return false;
        }

        logger.debug("Pre-authenticated user principal has changed to " + principal + " and will be reauthenticated");

        if (invalidateSessionOnPrincipalChange) {
            authenticationState.clearUserAuthentication();
        }

        return true;
    }

    private AuthenticationState getAuthenticationState() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        AuthenticationState authenticationState;

        if (authentication instanceof AuthenticationState) {
            authenticationState = (AuthenticationState) authentication;
        } else {
            authenticationState = new AuthenticationState(authentication);
        }
        return authenticationState;
    }

    /**
     * Puts the <code>Authentication</code> instance returned by the
     * authentication manager into the secure context.
     */
    protected void successfulUserAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication userAuthResult) {
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success: " + userAuthResult);
        }

        AuthenticationState authenticationState = getAuthenticationState();

        if (authenticationState == null) {
            authenticationState = new AuthenticationState(userAuthResult);
        } else {
            authenticationState.setUserAuthentication(userAuthResult);
        }
        SecurityContextHolder.getContext().setAuthentication(authenticationState);
        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authenticationState, this.getClass()));
        }
    }

    /**
     * Ensures the authentication object in the secure context is set to null when authentication fails.
     * <p/>
     * Caches the failure exception as a request attribute
     */
    protected void unsuccessfulUserAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        AuthenticationState authenticationState = getAuthenticationState();

        if (authenticationState == null) {
            return;
        }

        authenticationState.clearUserAuthentication();

        if (logger.isDebugEnabled()) {
            logger.debug("Cleared user security context due to exception", failed);
        }
        request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, failed);
    }

    /**
     * @param anApplicationEventPublisher The ApplicationEventPublisher to use
     */
    public void setApplicationEventPublisher(ApplicationEventPublisher anApplicationEventPublisher) {
        this.eventPublisher = anApplicationEventPublisher;
    }

    /**
     * @param authenticationDetailsSource The AuthenticationDetailsSource to use
     */
    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    protected AuthenticationDetailsSource<HttpServletRequest, ?> getAuthenticationDetailsSource() {
        return authenticationDetailsSource;
    }

    /**
     * If set to {@code true}, any {@code AuthenticationException} raised by the {@code AuthenticationManager} will be
     * swallowed, and the request will be allowed to proceed, potentially using alternative authentication mechanisms.
     * If {@code false} (the default), authentication failure will result in an immediate exception.
     *
     * @param shouldContinue set to {@code true} to allow the request to proceed after a failed authentication.
     */
    public void setContinueFilterChainOnUnsuccessfulAuthentication(boolean shouldContinue) {
        continueFilterChainOnUnsuccessfulAuthentication = shouldContinue;
    }

    /**
     * If set, the pre-authenticated principal will be checked on each request and compared
     * against the name of the current <tt>Authentication</tt> object. If a change is detected,
     * the user will be reauthenticated.
     *
     * @param checkForPrincipalChanges
     */
    public void setCheckForPrincipalChanges(boolean checkForPrincipalChanges) {
        this.checkForPrincipalChanges = checkForPrincipalChanges;
    }

    /**
     * If <tt>checkForPrincipalChanges</tt> is set, and a change of principal is detected, determines whether
     * any existing session should be invalidated before proceeding to authenticate the new principal.
     *
     * @param invalidateSessionOnPrincipalChange <tt>false</tt> to retain the existing session. Defaults to <tt>true</tt>.
     */
    public void setInvalidateSessionOnPrincipalChange(boolean invalidateSessionOnPrincipalChange) {
        this.invalidateSessionOnPrincipalChange = invalidateSessionOnPrincipalChange;
    }

    /**
     * Override to extract the principal information from the current request
     */
    protected abstract Object getPreAuthenticatedUserPrincipal(HttpServletRequest request);

    /**
     * Override to extract the credentials (if applicable) from the current request. Should not return null for a valid
     * principal, though some implementations may return a dummy value.
     */
    protected abstract Object getPreAuthenticatedUserCredentials(HttpServletRequest request);
}
