package com.rooxteam.uidm.sdk.spring.policy;

import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.uidm.sdk.spring.UidmSdkSpringLogger;
import com.rooxteam.uidm.sdk.spring.authentication.AuthenticationState;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

final class PermissionsEvaluationServiceImpl implements PermissionsEvaluationService {

    private final AuthenticationAuthorizationLibrary aal;

    private final RequestMappingHandlerMapping handlerMapping;

    private final ApplicationContext applicationContext;

    public PermissionsEvaluationServiceImpl(AuthenticationAuthorizationLibrary aal,
                                            RequestMappingHandlerMapping handlerMapping,
                                            ApplicationContext applicationContext) {
        this.aal = aal;
        this.handlerMapping = handlerMapping;
        this.applicationContext = applicationContext;
    }

    @Override
    public Map<String, Set<RequestMethod>> evaluate(Authentication authentication) {
        Principal aalPrincipal = null;
        if (authentication instanceof AuthenticationState) {
            aalPrincipal = (Principal) ((AuthenticationState)authentication).getAttributes().get("aalPrincipal");
        }
        return evaluate(authentication, aalPrincipal);
    }

    @Override
    public Map<String, Set<RequestMethod>> evaluate(Authentication authentication, final Principal principal) {
        final Map<EvaluationRequest, EvaluationResponse> evaluatedPolicies;
        if (principal == null) {
            evaluatedPolicies = Collections.emptyMap();
        } else {
            Set<EvaluationRequest> policies = collectPoliciesForHandledMethods(authentication, principal);
            evaluatedPolicies = aal.evaluatePolicies(principal, new ArrayList<EvaluationRequest>(policies));
        }

        final Map<String, Set<RequestMethod>> result = new LinkedHashMap<String, Set<RequestMethod>>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();

            PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
            if (preAuthorize == null) {
                continue;
            }
            AtomicBoolean isAllowed = new AtomicBoolean(false);
            try {
                InvocationRootObject invocationRootObject = new InvocationRootObject(authentication, principal) {
                    @Override
                    public boolean isAllowed(String resource, String operation, Map<String, ?> envParameters) {
                        if (!isAuthenticated()) {
                            return false;
                        }
                        EvaluationRequest key = new EvaluationRequest(resource, operation, envParameters);
                        EvaluationResponse evaluationResponse = evaluatedPolicies.get(key);
                        return evaluationResponse != null && evaluationResponse.getDecision() == Decision.Permit;
                    }
                };
                InjectedRootEvaluationContext dummyContext = new InjectedRootEvaluationContext(applicationContext, invocationRootObject);
                isAllowed.set(new SpelExpressionParser().parseExpression(preAuthorize.value()).getValue(dummyContext, Boolean.class));

            } catch (Throwable e) {
                UidmSdkSpringLogger.LOG.debug(preAuthorize.value(), e);
                continue;
            }

            if (isAllowed.get()) {
                RequestMappingInfo mappingInfo = entry.getKey();
                for (String pattern : mappingInfo.getPatternsCondition().getPatterns()) {
                    Set<RequestMethod> methods = result.get(pattern);
                    if (methods == null) {
                        methods = new LinkedHashSet<RequestMethod>();
                        result.put(pattern, methods);
                    }

                    methods.addAll(mappingInfo.getMethodsCondition().getMethods());
                }
            }
        }

        return result;
    }

    private Set<EvaluationRequest> collectPoliciesForHandledMethods(final Authentication authentication, final Principal principal) {
        final Set<EvaluationRequest> policies = new HashSet<EvaluationRequest>();
        InvocationRootObject invocationRootObject = new InvocationRootObject(authentication, principal) {
            @Override
            public boolean isAllowed(String resource, String operation, Map<String, ?> envParameters) {
                if (!isAuthenticated()) {
                    return false;
                }
                policies.add(new EvaluationRequest(resource, operation, envParameters));
                return true;
            }
        };

        InjectedRootEvaluationContext dummyContext = new InjectedRootEvaluationContext(applicationContext, invocationRootObject);
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
            if (preAuthorize != null) {
                try {
                    Boolean ignored = new SpelExpressionParser()
                            .parseExpression(preAuthorize.value())
                            .getValue(dummyContext, Boolean.class);
                } catch (Throwable e) {
                    UidmSdkSpringLogger.LOG.debug(preAuthorize.value(), e);
                }
            }
        }
        return policies;
    }

}
