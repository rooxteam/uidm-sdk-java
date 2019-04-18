package com.rooxteam.uidm.sdk.spring.authentication;

import lombok.Setter;
import org.jboss.logging.MDC;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UidmUserPreAuthenticationFilter - фильтр, обеспечивает sso аутентификацию по токену используя сервер UIDM.
 */
public class UidmUserPreAuthenticationFilter extends AbstractUserPreAuthenticatedProcessingFilter implements Ordered, EnvironmentAware {

    private static final Pattern TOKEN_VALIDATION_PATTERN = Pattern.compile("Bearer ([a-zA-Z]+)_([\\.\\d]+)_(.+)");

    public static final String SSO = "sso";
    public static final String TOKEN_VERSION_1_0 = "1.0";

    /**
     * Список атрибутов из Principal.sharedIdentityProperties которые надо сложить в MDC
     */
    public static final String PRINCIPAL_ATTRIBUTES_EXPOSE_TO_MDC = "com.rooxteam.aal.mdc.principal_attributes_to_expose";

    private SsoAuthorizationClient ssoAuthorizationClient;

    @Setter
    private Environment environment;

    public UidmUserPreAuthenticationFilter(SsoAuthorizationClient ssoAuthorizationClient) {
        this.ssoAuthorizationClient = ssoAuthorizationClient;
    }

    @Override
    public Object getPreAuthenticatedUserPrincipal(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return null;
        }
        AuthenticationState authenticationState = ssoAuthorizationClient.validate(request, token);
        setupMDC(authenticationState);
        return authenticationState;
    }

    @Override
    public Object getPreAuthenticatedUserCredentials(HttpServletRequest request) {
        // not applicable
        return "N/A";
    }

    private String extractToken(HttpServletRequest request) {
        String authenticationToken = request.getHeader("Authorization");
        if (authenticationToken == null) {
            return null;
        }

        Matcher matcher = TOKEN_VALIDATION_PATTERN.matcher(authenticationToken);
        if (!matcher.matches() || matcher.groupCount() != 3) {
            return null;
        }

        String authMethod = matcher.group(1);
        String version = matcher.group(2);
        String token = matcher.group(3);

        if (!authMethod.equalsIgnoreCase(SSO)) {
            return null;
        }
        if (!version.equalsIgnoreCase(TOKEN_VERSION_1_0)) {
            return null;
        }

        return token;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
        clearMDC();
    }

    private void setupMDC(AuthenticationState authenticationState) {
        if (authenticationState == null) return;
        if (!authenticationState.isAuthenticated()) {
            // we specially don't write anything in mdc in case of failure
            return;
        }
        String[] exposeAttributeNames = environment.getProperty(PRINCIPAL_ATTRIBUTES_EXPOSE_TO_MDC,
                String[].class,
                new String[0]);
        for (String exposeAttributeName : exposeAttributeNames) {
            MDC.put("pa-" + exposeAttributeName, authenticationState.getAttributes().get(exposeAttributeName));
        }
    }

    private void clearMDC() {
        String[] exposeAttributeNames = environment.getProperty(PRINCIPAL_ATTRIBUTES_EXPOSE_TO_MDC,
                String[].class,
                new String[0]);
        for (String exposeAttributeName : exposeAttributeNames) {
            MDC.remove("pa-" + exposeAttributeName);
        }
    }

    @Override
    public int getOrder() {
        return 150;
    }
}
