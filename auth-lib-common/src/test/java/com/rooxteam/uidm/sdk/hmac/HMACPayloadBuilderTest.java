package com.rooxteam.uidm.sdk.hmac;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HMACPayloadBuilderTest {

    public static final String TEST_SIGNATURE_HEADER = "version=1; timestamp=1675766946; alg=hmac-sha256; signature=cr6g7jqkyEsu9KiupdACGrv+60qGL8y1QxQOEontNgk=";
    public static final String TEST_JSON = "{\"agreement\": \"true\", \"qrId\": \"AB1S006DNHNA8QG892R901U9FSQQFFGD\", \"subscription\": {\"subscriptionName\": \"autotest_first_positive_flow\", \"cbAccount\": \"40820810150010014388\", \"cardId\": \"2084994\", \"monthlyLimit\": {\"amount\": 100000, \"currency\": \"RUB\"}}}";
    @Mock
    private ContentCachingRequestWrapper request;
    @Mock
    private Principal principal;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void basicTest() {
        when(request.getHeader(eq(ConfigKeys.REQUEST_SIGNATURE_HEADER))).thenReturn(TEST_SIGNATURE_HEADER);
        when(request.getMethod()).thenReturn("POST");
        when(request.getContentAsByteArray()).thenReturn(TEST_JSON.getBytes(StandardCharsets.UTF_8));
        when(request.getCharacterEncoding()).thenReturn(null);

        when(request.getServerName()).thenReturn("sso-uni.demo.rooxteam.com");
        when(request.getContextPath()).thenReturn("/webapi-1.0");
        when(request.getServletPath()).thenReturn("/payments/phone/subscriptions");
        when(request.getQueryString()).thenReturn(null);
        when(request.getContentType()).thenReturn("application/json");
        when(request.getMethod()).thenReturn("POST");
        when(principal.getProperty("realm")).thenReturn("/customer");
        when(principal.getProperty("sub")).thenReturn("bis_____172345");

        Map<String, ?> map = HMACPayloadBuilder.build(principal, request);
        assertNotNull(map);
        assertFalse(map.isEmpty());
        assertEquals("sso-uni.demo.rooxteam.com\n" +
                "/webapi-1.0/payments/phone/subscriptions\n" +
                "\n" +
                "application/json\n" +
                "POST\n" +
                "{\"agreement\": \"true\", \"qrId\": \"AB1S006DNHNA8QG892R901U9FSQQFFGD\", \"subscription\": {\"subscriptionName\": \"autotest_first_positive_flow\", \"cbAccount\": \"40820810150010014388\", \"cardId\": \"2084994\", \"monthlyLimit\": {\"amount\": 100000, \"currency\": \"RUB\"}}}\n" +
                "1675766946\n" +
                "bis_____172345\n" +
                "/customer\n", map.get("hmacPayload"));

        verifyInteractions();
        verify(request).getCharacterEncoding();
    }

    @Test
    public void testNullValues() {
        when(request.getHeader(eq(ConfigKeys.REQUEST_SIGNATURE_HEADER))).thenReturn(TEST_SIGNATURE_HEADER);
        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/webapi-1.0");
        when(request.getServletPath()).thenReturn("/payments/phone/subscriptions");

        Map<String, ?> map = HMACPayloadBuilder.build(principal, request);
        assertNotNull(map);
        assertFalse(map.isEmpty());
        assertEquals("\n" +
                "/webapi-1.0/payments/phone/subscriptions\n" +
                "\n" +
                "\n" +
                "POST\n" +
                "\n" +
                "1675766946\n" +
                "\n" +
                "\n", map.get("hmacPayload"));

        verifyInteractions();
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(request, principal);
    }

    private void verifyInteractions() {
        verify(request).getHeader(ConfigKeys.REQUEST_SIGNATURE_HEADER);
        verify(request).getContentAsByteArray();
        verify(request).getServerName();
        verify(request).getContextPath();
        verify(request).getServletPath();
        verify(request).getPathInfo();
        verify(request).getQueryString();
        verify(request).getContentType();
        verify(request).getMethod();
        verify(principal).getProperty("realm");
        verify(principal).getProperty("sub");
        verify(request).getHeader("X-Request-Signature");
    }
}