package com.rooxteam.sso.aal.client;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.sso.aal.client.exception.NotSupportedException;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.utils.SsoPolicyDecisionUtils;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.policy.PolicyDecision;
import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.client.PolicyEvaluator;
import com.sun.identity.policy.client.PolicyEvaluatorFactory;
import com.sun.identity.shared.locale.L10NMessageImpl;
import com.sun.istack.internal.Nullable;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Collections;
import java.util.Map;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * @author Dmitry Tikhonov
 */
public class SsoAuthorizationClientBySSOToken implements SsoAuthorizationClient {

    private static final String USER_ORGANIZATION_NAME = "customer";
    private static final String AUTHENTICATION_INDEX_NAME = "uidm";
    private static final String WEB_AGENT_SERVICE_NAME = "iPlanetAMWebAgentService";

    private static final String TOKEN_INFO_PATH = "/oauth2/tokeninfo";
    private final Configuration config;
    private CloseableHttpClient httpClient;

    public SsoAuthorizationClientBySSOToken(Configuration rooxConfig, CloseableHttpClient httpClient) {
        config = rooxConfig;
        this.httpClient = httpClient;
    }

    @Nullable
    public SSOToken authenticateByJwt(String jwt) {
        try {
            AuthContext authContext = initAuthContext();
            authContext.login(AuthContext.IndexType.SERVICE, AUTHENTICATION_INDEX_NAME, new String[]{jwt});
            if (authContext.getStatus().equals(AuthContext.Status.SUCCESS)) {
                return authContext.getSSOToken();
            } else {
                if (authContext.getLoginException() != null) {
                    LOG.errorLogin(authContext.getLoginException());
                } else {
                    LOG.errorUnexpectedStateAfterAuthenticationInSso(authContext.getStatus().toString());
                }
            }
        } catch (AuthLoginException e) {
            LOG.errorAuthLogin(e);
        } catch (L10NMessageImpl l10NMessage) {
            LOG.errorI10n(l10NMessage);
        }

        return null;
    }

    public AuthContext initAuthContext() throws AuthLoginException {
        return new AuthContext(USER_ORGANIZATION_NAME, httpClient);
    }

    @Override
    public EvaluationResponse isActionOnResourceAllowedByPolicy(Principal subject, String token, String resource, String method, Map<String, ?> env) {
        SSOToken ssoToken = getSsoToken(subject);

        if (ssoToken == null) {
            LOG.warnNullSsoToken();
            return new EvaluationResponse(Decision.Deny);
        }

        if (StringUtils.isEmpty(resource)) {
            LOG.warnNullResource();
            throw new IllegalArgumentException("Resource name is not supplied");
        }

        if (StringUtils.isEmpty(method)) {
            LOG.warnNullMethod();
            throw new IllegalArgumentException("Method name is not supplied");
        }

        try {
            PolicyEvaluator policyEvaluator = PolicyEvaluatorFactory.getInstance().getPolicyEvaluator(WEB_AGENT_SERVICE_NAME);
            PolicyDecision policyDecision = policyEvaluator.getPolicyDecision(ssoToken, resource, Collections.singleton(method));

            if (policyDecision.getActionDecisions().isEmpty()) {
                boolean allow = config.getBoolean(ConfigKeys.ALLOW_ACCESS_WITHOUT_POLICY, ConfigKeys.ALLOW_ACCESS_WITHOUT_POLICY_DEFAULT);
                return new EvaluationResponse(Decision.fromAllow(allow), Collections.<String, String>emptyMap());
            } else {
                return SsoPolicyDecisionUtils.toEvaluationResponse(policyDecision, method);
            }
        } catch (PolicyException e) {
            LOG.errorCreateEvaluator(WEB_AGENT_SERVICE_NAME, e);
            throw new IllegalStateException("Unable to create SSO policy evaluator");
        } catch (SSOException e) {
            LOG.errorSsoTokenInvalid(WEB_AGENT_SERVICE_NAME, e);
            throw new IllegalArgumentException("Invalid SSO token (session expired?)");
        }
    }

    @Nullable
    private SSOToken getSsoToken(Principal subject) {
        SSOToken ssoToken = (SSOToken) subject.getProperty(PropertyScope.PRIVATE_IDENTITY_PARAMS, Principal.SESSION_PARAM);

        if (ssoToken != null) {
            LOG.traceHasSSOTokenInPrincipal();
            try {
                if (ssoToken.getTimeLeft() < 0) {
                    LOG.traceSSOTokenExpired();
                    ssoToken = null;
                } else {
                    LOG.traceSSOTokenInPrincipalNotExpired();
                }
            } catch (Exception e) {
                LOG.traceFailedToGetSSOTimeLeft(e);
            }
        }
        if (ssoToken == null) {
            String jwt = null;
            if (subject instanceof PrincipalImpl) {
                jwt = ((PrincipalImpl) subject).getPrivateJwtToken();
            } else {
                jwt = subject.getJwtToken();
            }
            LOG.traceNoSSOTokenInPrincipal();
            ssoToken = authenticateByJwt(jwt);
            if (ssoToken != null) {
                subject.setProperty(PropertyScope.PRIVATE_IDENTITY_PARAMS, Principal.SESSION_PARAM, ssoToken);
            }
        }

        return ssoToken;
    }

    /**
     * Token validation
     *
     * @param token Token value
     * @return True if token is valid
     */
    @Override
    public Principal validate(final String token) {
        throw new NotSupportedException();
    }
}
