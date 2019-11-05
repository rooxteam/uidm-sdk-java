package com.rooxteam.uidm.sdk.spring.authentication;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.Configuration;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LegacySharedSecretSystemPreAuthenticationFilter - фильтр, обеспечивает системную аутентификацию через строку -
 * разделяемый секрет, который хранится в конфиге клиента и сервера.
 * <p>
 * Для использования подключите фильтр в цепочку и задайте конфигурационное свойство {@value ConfigKeys#INTERNAL_TOKEN_KEY}
 */
public class LegacySharedSecretSystemPreAuthenticationFilter extends AbstractUserPreAuthenticatedProcessingFilter implements Ordered, EnvironmentAware {


    private final Pattern tokenValidationPattern = Pattern.compile("Bearer ([a-zA-Z]+)_([\\.\\d]+)_(.+)");

    private final Configuration configuration;

    private final String internalToken;


    public LegacySharedSecretSystemPreAuthenticationFilter(Configuration config) {
        this.configuration = Objects.requireNonNull(config);
        internalToken = configuration.getString(ConfigKeys.INTERNAL_TOKEN_KEY);
    }

    @Override
    public Object getPreAuthenticatedUserPrincipal(HttpServletRequest request) {
        if (internalToken == null) {
            return null;
        }
        String authenticationToken = request.getHeader("Authorization");
        if (authenticationToken == null) {
            return null;
        }

        Matcher matcher = tokenValidationPattern.matcher(authenticationToken);
        if (!matcher.matches() || matcher.groupCount() != 3) {
            return null;
        }

        String authMethod = matcher.group(1);
        String version = matcher.group(2);
        String token = matcher.group(3);

        if (!authMethod.equalsIgnoreCase("internal")) {
            return null;
        }

        if (!version.equalsIgnoreCase("1.0")) {
            return null;
        }

        if (!token.equals(internalToken)) {
            return null;
        }


        AuthenticationState userAuthState = new AuthenticationState(new SimpleGrantedAuthority("ROLE_SYSTEM"));
        userAuthState.setAuthenticated(true);
        userAuthState.setPrincipal("SYSTEM");
        userAuthState.setAuthLevel(9);
        return userAuthState;
    }

    @Override
    public Object getPreAuthenticatedUserCredentials(HttpServletRequest request) {
        // not applicable
        return "N/A";
    }


    @Override
    public int getOrder() {
        return 10;
    }
}
