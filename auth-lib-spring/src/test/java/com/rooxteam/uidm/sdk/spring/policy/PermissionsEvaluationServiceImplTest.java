package com.rooxteam.uidm.sdk.spring.policy;

import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.uidm.sdk.spring.authentication.AuthenticationState;
import com.rooxteam.uidm.sdk.spring.configuration.UidmSdkConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * @author sergey.syroezhkin
 * @since 14.01.2021
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PermissionsEvaluationServiceImplTest.TestConfiguration.class})
public class PermissionsEvaluationServiceImplTest {

    @Autowired
    private PermissionsEvaluationService permissionsEvaluationService;

    @Autowired
    private AuthenticationAuthorizationLibrary aal;

    private static Map<RequestMappingInfo, HandlerMethod> currentMockHandlerMethods;

    @Before
    public void setUp() {
        reset(aal);
    }

    @Test
    public void evaluate_isAuthenticated() {
        Principal principal = mockPrincipal(true);

        List<MethodData> mockMethods = new ArrayList<MethodData>();
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo", "isAuthenticated()"));
        mockMethodHandlerWithSpEl(mockMethods);

        Map<String, Set<RequestMethod>> result = permissionsEvaluationService.evaluate(new AuthenticationState(), principal);

        assertNotNull(result);
        assertEquals(1, result.entrySet().size());
        assertTrue(result.containsKey("/api/demo"));
        assertCollectionContains(result.get("/api/demo"), RequestMethod.GET);
    }

    @Test
    public void evaluate_notAuthenticated() {
        Principal principal = mockPrincipal(false);

        List<MethodData> mockMethods = new ArrayList<MethodData>();
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo", "isAuthenticated()"));
        mockMethodHandlerWithSpEl(mockMethods);

        Map<String, Set<RequestMethod>> result = permissionsEvaluationService.evaluate(new AuthenticationState(), principal);

        assertNotNull(result);
        assertEquals(0, result.entrySet().size());
    }

    @Test
    public void evaluate_positive_isAllowed() {
        Principal principal = mockPrincipal(true);

        List<MethodData> mockMethods = new ArrayList<MethodData>();
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo", "@uidmAuthz.isAllowed('/demo','GET')"));
        mockMethodHandlerWithSpEl(mockMethods);

        List<MethodData> policyMethods = new ArrayList<MethodData>();
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo", null));
        mockPolicies(policyMethods);

        Map<String, Set<RequestMethod>> result = permissionsEvaluationService.evaluate(new AuthenticationState(), principal);

        assertNotNull(result);
        assertEquals(1, result.entrySet().size());
        assertTrue(result.containsKey("/api/demo"));
        assertCollectionContains(result.get("/api/demo"), RequestMethod.GET);
    }

    @Test
    public void evaluate_isResourceAllowed() {
        Principal principal = mockPrincipal(true);

        List<MethodData> mockMethods = new ArrayList<MethodData>();
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo", "isResourceAllowed('/demo','GET')"));
        mockMethodHandlerWithSpEl(mockMethods);

        List<MethodData> policyMethods = new ArrayList<MethodData>();
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo", null));
        mockPolicies(policyMethods);

        Map<String, Set<RequestMethod>> result = permissionsEvaluationService.evaluate(new AuthenticationState(), principal);

        assertNotNull(result);
        assertEquals(1, result.entrySet().size());
        assertTrue(result.containsKey("/api/demo"));
        assertCollectionContains(result.get("/api/demo"), RequestMethod.GET);
    }

    @Test
    public void evaluate_LogicalOrInSpel_positive() {
        Principal principal = mockPrincipal(true);

        List<MethodData> mockMethods = new ArrayList<MethodData>();
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo", "@uidmAuthz.isAllowed('/demo1','GET') || @uidmAuthz.isAllowed('/demo2','GET')"));
        mockMethodHandlerWithSpEl(mockMethods);

        List<MethodData> policyMethods = new ArrayList<MethodData>();
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo1", null));
        mockPolicies(policyMethods);

        Map<String, Set<RequestMethod>> result = permissionsEvaluationService.evaluate(new AuthenticationState(), principal);

        assertNotNull(result);
        assertEquals(1, result.entrySet().size());
        assertTrue(result.containsKey("/api/demo"));
        assertCollectionContains(result.get("/api/demo"), RequestMethod.GET);
    }

    @Test
    public void evaluate_LogicalOrInSpel_positive2() {
        Principal principal = mockPrincipal(true);

        List<MethodData> mockMethods = new ArrayList<MethodData>();
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo", "@uidmAuthz.isAllowed('/demo1','GET') || @uidmAuthz.isAllowed('/demo2','GET')"));
        mockMethodHandlerWithSpEl(mockMethods);

        List<MethodData> policyMethods = new ArrayList<MethodData>();
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo1", null));
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo2", null));
        mockPolicies(policyMethods);

        Map<String, Set<RequestMethod>> result = permissionsEvaluationService.evaluate(new AuthenticationState(), principal);

        assertNotNull(result);
        assertEquals(1, result.entrySet().size());
        assertTrue(result.containsKey("/api/demo"));
        assertCollectionContains(result.get("/api/demo"), RequestMethod.GET);
    }

    @Test
    public void evaluate_LogicalOrInSpel_negative() {
        Principal principal = mockPrincipal(true);

        List<MethodData> mockMethods = new ArrayList<MethodData>();
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo", "@uidmAuthz.isAllowed('/demo1','GET') || @uidmAuthz.isAllowed('/demo2','GET')"));
        mockMethodHandlerWithSpEl(mockMethods);

        List<MethodData> policyMethods = new ArrayList<MethodData>();
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo3", null));
        mockPolicies(policyMethods);

        Map<String, Set<RequestMethod>> result = permissionsEvaluationService.evaluate(new AuthenticationState(), principal);

        assertNotNull(result);
        assertEquals(0, result.entrySet().size());
    }

    @Test
    public void evaluate_LogicalAndInSpel_positive() {
        Principal principal = mockPrincipal(true);

        List<MethodData> mockMethods = new ArrayList<MethodData>();
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo", "@uidmAuthz.isAllowed('/demo1','GET') && @uidmAuthz.isAllowed('/demo2','GET')"));
        mockMethodHandlerWithSpEl(mockMethods);

        List<MethodData> policyMethods = new ArrayList<MethodData>();
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo1", null));
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo2", null));
        mockPolicies(policyMethods);

        Map<String, Set<RequestMethod>> result = permissionsEvaluationService.evaluate(new AuthenticationState(), principal);

        assertNotNull(result);
        assertEquals(1, result.entrySet().size());
        assertTrue(result.containsKey("/api/demo"));
        assertCollectionContains(result.get("/api/demo"), RequestMethod.GET);
    }

    @Test
    public void evaluate_LogicalAndInSpel_negative() {
        Principal principal = mockPrincipal(true);

        List<MethodData> mockMethods = new ArrayList<MethodData>();
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo", "@uidmAuthz.isAllowed('/demo1','GET') && @uidmAuthz.isAllowed('/demo2','GET')"));
        mockMethodHandlerWithSpEl(mockMethods);

        List<MethodData> policyMethods = new ArrayList<MethodData>();
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo1", null));
        mockPolicies(policyMethods);

        Map<String, Set<RequestMethod>> result = permissionsEvaluationService.evaluate(new AuthenticationState(), principal);

        assertNotNull(result);
        assertEquals(0, result.entrySet().size());
    }

    @Test
    public void evaluate_multipleEndpoints() {
        Principal principal = mockPrincipal(true);

        List<MethodData> mockMethods = new ArrayList<MethodData>();
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo1", "@uidmAuthz.isAllowed('/demo1','GET')"));
        mockMethods.add(new MethodData(RequestMethod.POST, "/api/demo1", "@uidmAuthz.isAllowed('/demo1','POST')"));
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo2", "@uidmAuthz.isAllowed('/demo2','GET')"));
        mockMethods.add(new MethodData(RequestMethod.GET, "/api/demo3", "@uidmAuthz.isAllowed('/demo3','GET')"));
        mockMethodHandlerWithSpEl(mockMethods);

        List<MethodData> policyMethods = new ArrayList<MethodData>();
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo1", null));
        policyMethods.add(new MethodData(RequestMethod.POST, "/demo1", null));
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo2", null));
        policyMethods.add(new MethodData(RequestMethod.GET, "/demo4", null));
        mockPolicies(policyMethods);

        Map<String, Set<RequestMethod>> result = permissionsEvaluationService.evaluate(new AuthenticationState(), principal);

        assertNotNull(result);
        assertEquals(2, result.entrySet().size());
        assertTrue(result.containsKey("/api/demo1"));
        assertCollectionContains(result.get("/api/demo1"), RequestMethod.GET, RequestMethod.POST);
        assertTrue(result.containsKey("/api/demo2"));
        assertCollectionContains(result.get("/api/demo2"), RequestMethod.GET);
    }

    private <T> void assertCollectionContains(Collection<T> collection, T... values) {
        assertNotNull(collection);
        assertEquals(collection.size(), values.length);
        for (T value: values) {
            assertTrue(collection.contains(value));
        }
    }

    private void mockPolicies(final List<MethodData> allowedMethods) {
        when(aal.evaluatePolicies(any(Principal.class), any(List.class))).thenAnswer(new Answer<Map<EvaluationRequest, EvaluationResponse>>() {
            @SuppressWarnings("unchecked")
            @Override
            public Map<EvaluationRequest, EvaluationResponse> answer(InvocationOnMock invocation) throws Throwable {
                List<EvaluationRequest> requestList = (List<EvaluationRequest>) invocation.getArguments()[1];
                Map<EvaluationRequest, EvaluationResponse> result = new HashMap<EvaluationRequest, EvaluationResponse>();
                for (EvaluationRequest request: requestList) {
                    boolean found = false;
                    for (MethodData method: allowedMethods) {
                        if (request.getActionName().equalsIgnoreCase(method.getMethod().name())
                                && request.getResourceName().equalsIgnoreCase(method.getPath())) {
                            found = true;
                        }
                    }
                    result.put(request, new EvaluationResponse(found ? Decision.Permit : Decision.Deny));
                }
                return result;
            }
        });
    }

    private Principal mockPrincipal(boolean authenticated) {
        Principal principal = mock(Principal.class);
        when(principal.isAnonymous()).thenReturn(!authenticated);
        return principal;
    }

    private void mockMethodHandlerWithSpEl(List<MethodData> methodData) {
        currentMockHandlerMethods = new HashMap<RequestMappingInfo, HandlerMethod>();
        for (final MethodData item: methodData) {
            PatternsRequestCondition patterns = new PatternsRequestCondition(item.getPath());
            RequestMethodsRequestCondition methods = new RequestMethodsRequestCondition(item.getMethod());
            RequestMappingInfo key = new RequestMappingInfo(patterns, methods, null, null, null, null, null);
            HandlerMethod value = mock(HandlerMethod.class);
            when(value.getMethodAnnotation(PreAuthorize.class)).thenReturn(new PreAuthorize() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }
                @Override
                public String value() {
                    return item.getSpel();
                }
            });
            currentMockHandlerMethods.put(key, value);
        }
    }

    @Data
    @AllArgsConstructor
    private class MethodData {
        private RequestMethod method;
        private String path;
        private String spel;
    }

    @Configuration
    @Import({ UidmSdkConfiguration.class })
    static class TestConfiguration {

        @Bean
        public PermissionsEvaluationService permissionsEvaluationService(AuthenticationAuthorizationLibrary aal,
                                                                         RequestMappingHandlerMapping handlerMapping,
                                                                         ApplicationContext applicationContext) {
            return new PermissionsEvaluationServiceImpl(aal, handlerMapping, applicationContext);
        }

        @Bean
        public RequestMappingHandlerMapping handlerMapping() {
            return new MockRequestMappingHandlerMapping();
        }

        @Bean
        @Primary
        public AuthenticationAuthorizationLibrary aal() {
            return mock(AuthenticationAuthorizationLibrary.class);
        }
    }

    static class MockRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        public Map<RequestMappingInfo, HandlerMethod> getHandlerMethods() {
            return currentMockHandlerMethods != null ? currentMockHandlerMethods
                    : new HashMap<RequestMappingInfo, HandlerMethod>();
        }

    }

}