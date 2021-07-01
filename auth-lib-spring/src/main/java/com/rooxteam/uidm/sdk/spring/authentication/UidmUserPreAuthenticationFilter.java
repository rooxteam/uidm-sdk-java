package com.rooxteam.uidm.sdk.spring.authentication;

import com.rooxteam.sso.aal.AalLogger;
import org.jboss.logging.MDC;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UidmUserPreAuthenticationFilter - фильтр, обеспечивает sso аутентификацию по токену используя сервер UIDM.
 */
public class UidmUserPreAuthenticationFilter extends AbstractUserPreAuthenticatedProcessingFilter implements Ordered, EnvironmentAware {

    private static final String TOKEN_PATTERN_PREFIX = "Bearer";
    private static final Pattern TOKEN_VALIDATION_PATTERN = Pattern.compile(String.format("%s ([a-zA-Z]+)_([\\.\\d]+)_(.+)", TOKEN_PATTERN_PREFIX));


    public static final String SSO = "sso";
    public static final String TOKEN_VERSION_1_0 = "1.0";

    private final SsoAuthorizationClient authorizationClient;
    private final UserPreAuthFilterSettings setting;

    public UidmUserPreAuthenticationFilter(SsoAuthorizationClient authorizationClient, UserPreAuthFilterSettings setting) {
        this.authorizationClient = authorizationClient;
        this.setting = setting;
    }

    @Override
    public Object getPreAuthenticatedUserPrincipal(HttpServletRequest request) {
        final String token = extractToken(request);
        if (token == null) {
            AalLogger.LOG.debug("No token in request. Skipping filter");
            return null;
        }

        final AuthenticationState authenticationState = authorizationClient.validate(request, token);
        setupMDC(request, authenticationState);

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
            final String tokenCookieName = setting.getCookieName();

            if (tokenCookieName != null) {
                final Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie ck : cookies) {
                        if (tokenCookieName.equals(ck.getName())) {
                            authenticationToken = String.format("%s %s", TOKEN_PATTERN_PREFIX, ck.getValue());
                        }
                    }
                } else {
                    return null;
                }
            }
        }

        if (authenticationToken == null) {
            return null;
        }

        final Matcher matcher = TOKEN_VALIDATION_PATTERN.matcher(authenticationToken);
        if (matcher.matches() && matcher.groupCount() == 3) {
            String authMethod = matcher.group(1);
            String version = matcher.group(2);
            String token = matcher.group(3);
            if (authMethod.equalsIgnoreCase(SSO) && version.equalsIgnoreCase(TOKEN_VERSION_1_0)) {
                return token;
            }
        } else if (authenticationToken.startsWith(TOKEN_PATTERN_PREFIX)) {
            return authenticationToken.substring(TOKEN_PATTERN_PREFIX.length()).trim();
        }

        return null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
        clearMDC(request);
    }

    private void setupMDC(HttpServletRequest request, AuthenticationState authenticationState) {
        if (authenticationState == null) return;
        if (!authenticationState.isAuthenticated()) {
            // we specially don't write anything in mdc in case of failure
            return;
        }

        final String[] exposeAttributeNames = setting.getPrincipalAttributesExposedToMDC();
        if (exposeAttributeNames != null) {
            for (String exposeAttributeName : exposeAttributeNames) {
                MDC.put("pa-" + exposeAttributeName, authenticationState.getAttributes().get(exposeAttributeName));
            }
        }
    }

    private void clearMDC(ServletRequest request) {
        final String[] exposeAttributeNames = setting.getPrincipalAttributesExposedToMDC();
        if (exposeAttributeNames != null) {
            for (String exposeAttributeName : exposeAttributeNames) {
                MDC.remove("pa-" + exposeAttributeName);
            }
        }
    }

    @Override
    public int getOrder() {
        return 150;
    }
}
