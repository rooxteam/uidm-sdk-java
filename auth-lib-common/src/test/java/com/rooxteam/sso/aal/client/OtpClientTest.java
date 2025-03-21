package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import com.rooxteam.sso.aal.otp.OtpResponse;
import com.rooxteam.sso.aal.otp.OtpStatus;
import com.rooxteam.sso.aal.userIp.UserIpProvider;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OtpClientTest {

    public static final String JWT_TOKEN = "eyAiYWxnIjogIkhTMjU2IiwgImN0eSI6ICJKV1QiLCAidHlwIjogImp3dCIgfQ.eyAidG9rZW5O" +
            "YW1lIjogImlkX3Rva2VuIiwgImF6cCI6ICJ3ZWJhcGkiLCAic3ViIjogIjI1MDExMDEwMDAxNDQ4IiwgIm1zaXNkbiI6ICIyNTAxMTAxMD" +
            "AwMTQ0OCIsICJpc3MiOiAiUGNyZkF1dGhlbnRpY2F0aW9uU2VydmljZSIsICJ2ZXIiOiAiMS4wIiwgImlhdCI6IDE0MzU1Njk1MzAsICJl" +
            "eHAiOiAxNDM1NTY5NTkwLCAidG9rZW5UeXBlIjogIkpXVFRva2VuIiwgInJlYWxtIjogIi9jdXN0b21lciIsICJhdXRoTGV2ZWwiOiBbICI" +
            "yIiBdLCAiYXVkIjogWyAid2ViYXBpIiBdLCAicmVuIjogMTQzNTU2OTU5MCwgImp0aSI6ICIyYzYzMmQxYS0yNTM1LTQzNjEtOGU5MC1iYz" +
            "Q5YjRlNDkxMmIiLCAiaW1zaSI6ICIyNTAxMTAxMDAwMTQ0OCIsICJhdGgiOiAxNDM1NTY5NTMwIH0.CTZDy6K3LzP6iUBrH5NXobEQHo6zi" +
            "q03p9RV3ugz3xg";

    public static final String ENTER_OTP_FORM_JSON = "{\n" +
            "    \"form\": {\n" +
            "        \"errors\": [],\n" +
            "        \"name\": \"otpForm\",\n" +
            "        \"fields\": {\n" +
            "            \"otpCode\": {\n" +
            "                \"constraints\": [\n" +
            "                    {\n" +
            "                        \"name\": \"NotNull\"\n" +
            "                    }\n" +
            "                ]\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    \"view\": {\n" +
            "        \"otpCodeAvailableAttempts\": 3,\n" +
            "        \"msisdn\": \"25011010001448\",\n" +
            "        \"blockedFor\": null\n" +
            "    },\n" +
            "    \"serverUrl\": \"otp-sms\",\n" +
            "    \"step\": \"enter_otp_form\",\n" +
            "    \"execution\": \"e2s1\"\n" +
            "}";

    public static final String ENTER_OTP_FORM_WITHOUT_ERRORS_JSON = "{\n" +
            "    \"form\": {\n" +
            "        \"name\": \"otpForm\",\n" +
            "        \"fields\": {\n" +
            "            \"otpCode\": {\n" +
            "                \"constraints\": [\n" +
            "                    {\n" +
            "                        \"name\": \"NotNull\"\n" +
            "                    }\n" +
            "                ]\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    \"view\": {\n" +
            "        \"otpCodeAvailableAttempts\": 3,\n" +
            "        \"msisdn\": \"25011010001448\",\n" +
            "        \"blockedFor\": null\n" +
            "    },\n" +
            "    \"serverUrl\": \"otp-sms\",\n" +
            "    \"step\": \"enter_otp_form\",\n" +
            "    \"execution\": \"e2s1\"\n" +
            "}";

    public static final String UNKNOWN_JSON = "{\"error\":{\"code\":403,\"message\":\"Expected CSRF token not found. Has your session expired?\"}}";

    private OtpClient otpClient;
    private CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);

    @Before
    public void setUp() {
        reset(mockHttpClient);

        BaseConfiguration configuration = new BaseConfiguration();
        configuration.addProperty("com.rooxteam.sso.endpoint", "http://example.com");
        otpClient = new OtpClient(ConfigurationBuilder.fromApacheCommonsConfiguration(configuration),
                mockHttpClient, mock(UserIpProvider.class));
    }

    @Test
    public void send_otp_correctly() throws IOException {
        CloseableHttpResponse mockHttpResponse = CloseableHttpResponse.adapt(mock(ClassicHttpResponse.class));
        HttpEntity mockHttpEntity = mock(HttpEntity.class);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getCode()).thenReturn(200);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(ENTER_OTP_FORM_JSON.getBytes());
        when(mockHttpEntity.getContent()).thenReturn(inputStream);
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpContext.class))).thenReturn(mockHttpResponse);

        OtpResponse otpResponse = otpClient.sendOtp("/customer", ENTER_OTP_FORM_JSON);
        assertNotNull(otpResponse);
        assertEquals(OtpStatus.OTP_REQUIRED, otpResponse.getStatus());

        verify(mockHttpEntity, times(1)).getContent();
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class), any(HttpContext.class));
    }

    @Test
    public void send_otp_without_errors() throws IOException {
        CloseableHttpResponse mockHttpResponse = CloseableHttpResponse.adapt(mock(ClassicHttpResponse.class));
        HttpEntity mockHttpEntity = mock(HttpEntity.class);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getCode()).thenReturn(200);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(ENTER_OTP_FORM_WITHOUT_ERRORS_JSON.getBytes());
        when(mockHttpEntity.getContent()).thenReturn(inputStream);
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpContext.class))).thenReturn(mockHttpResponse);

        OtpResponse otpResponse = otpClient.sendOtp("/customer", JWT_TOKEN);
        assertNotNull(otpResponse);
        assertEquals(OtpStatus.OTP_REQUIRED, otpResponse.getStatus());

        verify(mockHttpEntity, times(1)).getContent();
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class), any(HttpContext.class));
    }

    @Test
    public void handle_unknown_response_from_sso_while_sending_otp() throws IOException {
        CloseableHttpResponse mockHttpResponse = CloseableHttpResponse.adapt(mock(ClassicHttpResponse.class));
        HttpEntity mockHttpEntity = mock(HttpEntity.class);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpResponse.getCode()).thenReturn(200);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(UNKNOWN_JSON.getBytes());
        when(mockHttpEntity.getContent()).thenReturn(inputStream);
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpContext.class))).thenReturn(mockHttpResponse);

        OtpResponse otpResponse = otpClient.sendOtp("/customer", JWT_TOKEN);
        assertNotNull(otpResponse);
        assertEquals(OtpStatus.EXCEPTION, otpResponse.getStatus());

        verify(mockHttpEntity, times(1)).getContent();
        verify(mockHttpClient, times(1)).execute(any(HttpPost.class), any(HttpContext.class));
    }
}
