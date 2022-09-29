package com.rooxteam.uidm.sdk.spring.authentication;

import com.rooxteam.sso.aal.AalLogger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        if (requiresUserAuthentication()) {
            doAuthenticate((HttpServletRequest) request, (HttpServletResponse) response);
        }

        chain.doFilter(request, response);
    }

    /**
     * Do the actual authentication for a pre-authenticated user.
     */
    private void doAuthenticate(HttpServletRequest request,
                                HttpServletResponse response) {

        AuthenticationState principal = (AuthenticationState) getPreAuthenticatedUserPrincipal(request);

        if (principal == null) {
            unsuccessfulUserAuthentication(request, response);
            return;
        }
        successfulUserAuthentication(request, response, principal);
    }

    private boolean requiresUserAuthentication() {
        Authentication authenticationState = getAuthenticationState();

        if (authenticationState == null) {
            AalLogger.LOG.debug(this.getClass().getSimpleName() + " Filter will try to authenticate due to absent " +
                    "auth state");
            return true;
        }
        if (!authenticationState.isAuthenticated()) {
            AalLogger.LOG.debug(this.getClass().getSimpleName() + " Filter will try to authenticate due to not " +
                    "authenticated request state");
            return true;
        }
        return true;
    }

    private Authentication getAuthenticationState() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Puts the <code>Authentication</code> instance returned by the
     * authentication manager into the secure context.
     */
    protected void successfulUserAuthentication(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication userAuthResult) {
        AalLogger.LOG.debugFilterAuthenticationSuccess(this.getClass().getSimpleName(), userAuthResult.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(userAuthResult);
        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(userAuthResult,
                    this.getClass()));
        }
    }

    /**
     * Ensures the authentication object in the secure context is set to null when authentication fails.
     * <p/>
     * Caches the failure exception as a request attribute
     */
    protected void unsuccessfulUserAuthentication(HttpServletRequest request,
                                                  HttpServletResponse response) {
        AalLogger.LOG.traceFilterAuthenticationFailed(this.getClass().getSimpleName());
    }

    /**
     * @param anApplicationEventPublisher The ApplicationEventPublisher to use
     */
    public void setApplicationEventPublisher(ApplicationEventPublisher anApplicationEventPublisher) {
        this.eventPublisher = anApplicationEventPublisher;
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
