package com.rooxteam.sso.aal;

import com.rooxteam.sso.aal.client.OtpClient;
import com.rooxteam.sso.aal.client.SsoAuthenticationClient;
import com.rooxteam.sso.aal.client.SsoAuthorizationClient;
import com.rooxteam.sso.aal.otp.OtpFlowState;
import com.rooxteam.sso.aal.otp.OtpFlowStateImpl;
import com.rooxteam.sso.aal.otp.OtpResponse;
import com.rooxteam.sso.aal.otp.OtpResponseImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
public class AALTest {

    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;
    public static final int DEFAULT_TIMEOUT = 10;

    @Test
    public void authenticate_EmptyParamMap_IllegalArgumentException() {

        final SsoAuthorizationClient mockSsoAuthorizationClient = mock(SsoAuthorizationClient.class);
        final SsoAuthenticationClient mockSsoAuthenticationClient = mock(SsoAuthenticationClient.class);
        AuthenticationAuthorizationLibrary aal = new RooxAuthenticationAuthorizationLibrary(null, mockSsoAuthorizationClient, mockSsoAuthenticationClient,
                null, null, null, null, null, AuthorizationType.JWT);

        try {
            aal.authenticate(Collections.<String, Object>emptyMap(), DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
            fail();
        } catch (IllegalArgumentException ignored) {
            //expect this exception to be thrown
        }

        verify(mockSsoAuthenticationClient, times(0)).authenticate(anyMap());
    }

    @Test
    public void authenticate_byIpReturnNull_YotaPrincipalWithNullJwt() {

        final SsoAuthenticationClient mockSsoAuthenticationClient = mock(SsoAuthenticationClient.class);

        HashMap<String, Object> params = new HashMap<>();
        params.put("ip", "incorrect IP");
        when(mockSsoAuthenticationClient.authenticate(params)).thenReturn(null);

        params = new HashMap<>();
        params.put("ip", "incorrect IP");
        AuthenticationAuthorizationLibrary aal = new RooxAuthenticationAuthorizationLibrary(null, null, mockSsoAuthenticationClient,
                null, null, null, null, null, AuthorizationType.JWT);

        Principal yotaPrincipal = aal.authenticate(params, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);

        assertNull(yotaPrincipal);
    }

    @Test
    public void sendOtp_nullPrincipal_illegalArgumentException() {

        final OtpClient mockOtpClient = mock(OtpClient.class);
        AuthenticationAuthorizationLibrary aal = new RooxAuthenticationAuthorizationLibrary(null, null, null, null,
                mockOtpClient, null, null, null, AuthorizationType.JWT);

        try {
            aal.sendOtp(null, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    @Ignore
    public void sendOtp_principalWithNoMsisdn_IllegalArgumentException() {

        String token = "eyJhbGciOiJIUzI1NiIsImN0eSI6IkpXVCIsInR5cCI6Imp3dCJ9.eyJ0b2tlbk5hbWUiOiJpZF90b2tlbiIsImF6cCI6IndlYmFwaSIsInN1YiI6IjI1MDExMDEwMDAxNDQ4IiwiaXNzIjoiUGNyZkF1dGhlbnRpY2F0aW9uU2VydmljZSIsInZlciI6IjEuMCIsImlhdCI6MTQzODk1NTkzOSwiZXhwIjoxNDM4OTU1OTk5LCJ0b2tlblR5cGUiOiJKV1RUb2tlbiIsInJlYWxtIjoiL2N1c3RvbWVyIiwiYXV0aExldmVsIjpbIjIiXSwiYXVkIjpbIndlYmFwaSJdLCJyZW4iOjE0Mzg5NTU5OTksImp0aSI6ImQ4OTg4YzcwLTI1MTQtNDI1YS1iMTMzLTFkMmY1YTRiZWJhNyIsImltc2kiOiIyNTAxMTAxMDAwMTQ0OCIsImF0aCI6MTQzODk1NTkzOX0.MN_kJ2BPGdkDntmjzoOdRekrAgRHooCJKjrtgX6d5ic";

        Principal mockYotaPrincipal = mock(Principal.class);
        when(mockYotaPrincipal.getJwtToken()).thenReturn(token);

        final OtpClient mockOtpClient = mock(OtpClient.class);
        AuthenticationAuthorizationLibrary aal = new RooxAuthenticationAuthorizationLibrary(null, null, null, null,
                mockOtpClient, null, null, null, AuthorizationType.JWT);

        try {
            aal.sendOtp(mockYotaPrincipal, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void sendOtp_validPrincipal_otpResponseWithFlowState() {

        String token = "eyAiYWxnIjogIkhTMjU2IiwgImN0eSI6ICJKV1QiLCAidHlwIjogImp3dCIgfQ.eyAidG9rZW5OYW1lIjogImlkX3Rva2VuIiwgImF6cCI6ICJ3ZWJhcGkiLCAic3ViIjogIjI1MDExMDEwMDAxNDQ4IiwgIm1zaXNkbiI6ICIyNTAxMTAxMDAwMTQ0OCIsICJpc3MiOiAiUGNyZkF1dGhlbnRpY2F0aW9uU2VydmljZSIsICJ2ZXIiOiAiMS4wIiwgImlhdCI6IDE0Mzg3MDMyNzMsICJleHAiOiAxNDM4NzAzMzMzLCAidG9rZW5UeXBlIjogIkpXVFRva2VuIiwgInJlYWxtIjogIi9jdXN0b21lciIsICJhdXRoTGV2ZWwiOiBbICIyIiBdLCAiYXVkIjogWyAid2ViYXBpIiBdLCAicmVuIjogMTQzODcwMzMzMywgImp0aSI6ICIzZTI2MjEzOC1lNjc4LTQ0MDYtODBjNy04M2I0NzJmYmJlNzkiLCAiaW1zaSI6ICIyNTAxMTAxMDAwMTQ0OCIsICJhdGgiOiAxNDM4NzAzMjczIH0.pMqdpoGQisd2EPmF3duzftWG0v6U_LtM5qXV186-5xM";

        Principal mockYotaPrincipal = mock(Principal.class);
        when(mockYotaPrincipal.getJwtToken()).thenReturn(token);

        final OtpClient mockOtpClient = mock(OtpClient.class);
        OtpResponseImpl mockResponse = new OtpResponseImpl();
        OtpFlowStateImpl otpFlowState = new OtpFlowStateImpl();
        otpFlowState.setExecution("execution");
        otpFlowState.setServerUrl("serverUrl");
        otpFlowState.setCsrf("csrf");
        otpFlowState.setSessionId("sessionId");
        mockResponse.setOtpFlowState(otpFlowState);
        when(mockOtpClient.sendOtp(anyString()))
                .thenReturn(mockResponse);
        AuthenticationAuthorizationLibrary aal = new RooxAuthenticationAuthorizationLibrary(null, null, null, null,
                mockOtpClient, null, null, null, AuthorizationType.JWT);

        OtpResponse response = aal.sendOtp(mockYotaPrincipal, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);

        assertNull(response.getStatus());
        OtpFlowState otpFlowStateResult = response.getOtpFlowState();
        assertEquals(otpFlowStateResult.getCsrf(), "csrf");
        assertEquals(otpFlowStateResult.getExecution(), "execution");
        assertEquals(otpFlowStateResult.getServerUrl(), "serverUrl");
        assertEquals(otpFlowStateResult.getSessionId(), "sessionId");
    }
}

