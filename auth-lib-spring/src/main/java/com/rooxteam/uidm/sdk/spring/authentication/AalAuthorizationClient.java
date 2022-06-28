package com.rooxteam.uidm.sdk.spring.authentication;

import com.rooxteam.sso.aal.AalLogger;
import com.rooxteam.sso.aal.AnonymousPrincipalImpl;
import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.AuthorizationType;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.exception.AalException;
import com.rooxteam.uidm.sdk.spring.authorization.AalResourceValidation;
import lombok.Setter;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.rooxteam.sso.aal.ConfigKeys.AUTHORIZATION_TYPE;
import static com.rooxteam.sso.aal.ConfigKeys.AUTHORIZATION_TYPE_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.POLICIES_FOR_SYSTEM;
import static com.rooxteam.sso.aal.ConfigKeys.POLICIES_FOR_SYSTEM_DEFAULT;

/**
 * @author RooX Solutions
 */
public class AalAuthorizationClient implements SsoAuthorizationClient, AalResourceValidation, EnvironmentAware {

    public static final String AAL_PRINCIPAL_ATTRIBUTE_NAME = "aalPrincipal";
    public static final String EVALUATION_CLAIMS_ATTRIBUTE_NAME = "evaluationClaims";
    public static final String EVALUATION_ADVICES_ATTRIBUTE_NAME = "evaluationAdvices";

    @Setter
    private Environment environment;

    private final AuthenticationAuthorizationLibrary aal;

    public AalAuthorizationClient(AuthenticationAuthorizationLibrary aal) {
        this.aal = aal;
    }

    @Override
    public AuthenticationState validate(HttpServletRequest request, String jwt) {
        Principal principal = null;
        try {
            principal = aal.validate(request, jwt);
        } catch (Exception e) {
            AalLogger.LOG.errorAuthentication(e);
            return null;
        }
        if (principal == null) {
            return null;
        }

        AuthenticationState authenticationState = new AuthenticationState(getAuthorities(principal));
        authenticationState.setPrincipal((String) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "prn"));
        authenticationState.setClientSystem((String) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "client_id"));
        authenticationState.setRealm((String) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "realm"));
        List<String> authLevelList = (List<String>) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "authLevel");
        if (!authLevelList.isEmpty()) {
            authenticationState.setAuthLevel(Integer.valueOf(authLevelList.get(0)));
        }

        authenticationState.setAuthenticated(true);
        authenticationState.setCredentials(jwt);
        authenticationState.getAttributes().put(AAL_PRINCIPAL_ATTRIBUTE_NAME, principal);
        authenticationState.setModule((String) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "authType"));
        passPrincipalProperties(principal, authenticationState);
        return authenticationState;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Principal principal) {
        Object roles = principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "roles");

        if (roles == null) {
            // old or unspecified
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }

        Set<GrantedAuthority> result = new LinkedHashSet<GrantedAuthority>();
        for (String role : (List<String>) roles) {
            result.add(new SimpleGrantedAuthority(role));
        }

        return result;
    }

    @Override
    public Map<String, Set<String>> getAttributesIfAllowed(String ssoToken, String resource, String method, Map<String, Object> envParameters) {
        return null;
    }

    @Override
    public boolean isAllowed(String resource, String operation) {
        return isAllowed(resource, operation, null);
    }

    @Override
    public boolean isAllowed(String resource, String operation, Map<String, ?> envParameters) {
        if (aal == null) {
            return false;
        }
        Principal aalPrincipal;
        SecurityContext seco = SecurityContextHolder.getContext();
        if (seco == null) {
            aalPrincipal = new AnonymousPrincipalImpl();
        } else {
            Authentication authentication = seco.getAuthentication();
            if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
                aalPrincipal = new AnonymousPrincipalImpl();
            } else {
                AuthenticationState authState = (AuthenticationState) authentication;
                if (isDevToken(authState)) {
                    // If dev token is both enabled and presented,
                    // we mark all the checks as passed if SSO policy evaluation mode is on
                    // if local config policy mode is on we pass execution to AAL
                    String authzTypeString = environment.getProperty(AUTHORIZATION_TYPE, AUTHORIZATION_TYPE_DEFAULT);
                    AuthorizationType authorizationType = AuthorizationType.valueOf(authzTypeString);
                    if (authorizationType == AuthorizationType.CONFIG) {
                        aalPrincipal = reconstructPrincipalFromAuthState(authState);
                    } else {
                        return true;
                    }
                } else if (isSystemAuthenticated(authState) && !environment.getProperty(POLICIES_FOR_SYSTEM,
                        Boolean.class,
                        POLICIES_FOR_SYSTEM_DEFAULT)) {
                    return true;
                } else {
                    aalPrincipal = (Principal) authState.getAttributes().get(AAL_PRINCIPAL_ATTRIBUTE_NAME);
                    if (aalPrincipal == null) {
                        aalPrincipal = reconstructPrincipalFromAuthState(authState);
                    }
                }
            }
        }
        EvaluationResponse result = aal.evaluatePolicy(aalPrincipal, resource, operation, envParameters);
        if (seco != null) {
            Authentication authentication = seco.getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                AuthenticationState authState = (AuthenticationState) authentication;
                if (result.getClaims() != null && !result.getClaims().isEmpty()) {
                    authState.getAttributes().put(EVALUATION_CLAIMS_ATTRIBUTE_NAME, result.getClaims());
                }
                if (result.getAdvices() != null && !result.getAdvices().isEmpty()) {
                    authState.getAttributes().put(EVALUATION_ADVICES_ATTRIBUTE_NAME, result.getAdvices());
                }
            }
        }
        return result.getDecision().isPositive();
    }

    @Override
    public String postprocess(String resource, String operation, Map<String, ?> envParameters, String response) {
        if (aal == null) {
            throw new AalException("AAL is not configured");
        }
        Principal aalPrincipal;
        SecurityContext seco = SecurityContextHolder.getContext();
        if (seco == null) {
            aalPrincipal = new AnonymousPrincipalImpl();
        } else {
            Authentication authentication = seco.getAuthentication();
            if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
                aalPrincipal = new AnonymousPrincipalImpl();
            } else {
                AuthenticationState authState = (AuthenticationState) authentication;
                aalPrincipal = (Principal) authState.getAttributes().get(AAL_PRINCIPAL_ATTRIBUTE_NAME);
                if (aalPrincipal == null) {
                    aalPrincipal = reconstructPrincipalFromAuthState(authState);
                }
            }
        }
        return aal.postprocessPolicy(aalPrincipal, resource, operation, envParameters, response);
    }

    private Principal reconstructPrincipalFromAuthState(AuthenticationState authenticationState) {
        Principal aalPrincipal;// reconstruct principal from available auth state
        Map<String, Object> sharedIdentityProperties = new HashMap<String, Object>();
        // auth level
        Integer authLevel = authenticationState.getAuthLevel();
        if (authLevel == null) {
            authLevel = 0;
        }
        sharedIdentityProperties.put("authLevel", Collections.singletonList(String.valueOf(authLevel)));
        aalPrincipal = new PrincipalImpl((String) authenticationState.getCredentials(), sharedIdentityProperties,
                null);
        return aalPrincipal;
    }

    protected void passPrincipalProperties(Principal principal, AuthenticationState authenticationState) {
        for (Map.Entry<String, Object> property : principal.getProperties(PropertyScope.SHARED_IDENTITY_PARAMS).entrySet()) {
            authenticationState.getAttributes().put(property.getKey(), property.getValue());
        }
    }

    private boolean isDevToken(AuthenticationState authentication) {
        return authentication.isUserDev()
                && DevTokenUtils.isDevTokenEnabled(environment);
    }

    private boolean isSystemAuthenticated(AuthenticationState authentication) {
        return authentication.getAuthoritySet().contains("ROLE_SYSTEM");
    }
}
