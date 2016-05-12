package com.rooxteam.sso.aal;

import com.google.common.cache.Cache;
import com.rooxteam.sso.aal.client.SsoAuthenticationClient;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AalRenewTest {

    private static final String TOKEN_FOR_229_213_38_0 = "eyAiYWxnIjogIkhTMjU2IiwgImN0eSI6ICJKV1QiLCAidHlwIjogImp3dCIgfQ." +
            "eyAidG9rZW5OYW1lIjogImlkX3Rva2VuIiwgImF6cCI6ICJ3ZWJhcGkiLCAic3ViIjogIjI1MDExMDEwMDAxNDQ4IiwgIm1zaXNkbiI6ICIyNT" +
            "AxMTAxMDAwMTQ0OCIsICJpc3MiOiAiUGNyZkF1dGhlbnRpY2F0aW9uU2VydmljZSIsICJ2ZXIiOiAiMS4wIiwgImlhdCI6IDE0MzU1Njk1MzAs" +
            "ICJleHAiOiAxNDM1NTY5NTkwLCAidG9rZW5UeXBlIjogIkpXVFRva2VuIiwgInJlYWxtIjogIi9jdXN0b21lciIsICJhdXRoTGV2ZWwiOiBbIC" +
            "IyIiBdLCAiYXVkIjogWyAid2ViYXBpIiBdLCAicmVuIjogMTQzNTU2OTU5MCwgImp0aSI6ICIyYzYzMmQxYS0yNTM1LTQzNjEtOGU5MC1iYzQ5" +
            "YjRlNDkxMmIiLCAiaW1zaSI6ICIyNTAxMTAxMDAwMTQ0OCIsICJhdGgiOiAxNDM1NTY5NTMwIH0.CTZDy6K3LzP6iUBrH5NXobEQHo6ziq03p9" +
            "RV3ugz3xg";

    public static final String TOKEN_FOR_229_213_38_1 = "eyAiYWxnIjogIkhTMjU2IiwgImN0eSI6ICJKV1QiLCAidHlwIjogImp3dCIgfQ." +
            "eyAidG9rZW5OYW1lIjogImlkX3Rva2VuIiwgImF6cCI6ICJ3ZWJhcGkiLCAic3ViIjogIjI1MDExMDEwMDAxNDQ4IiwgIm1zaXNkbiI6ICIyN" +
            "TAxMTAxMDAwMTQ0OCIsICJpc3MiOiAiUGNyZkF1dGhlbnRpY2F0aW9uU2VydmljZSIsICJ2ZXIiOiAiMS4wIiwgImlhdCI6IDE0MzU1ODg0MT" +
            "gsICJleHAiOiAxNDM1NTg4NDc4LCAidG9rZW5UeXBlIjogIkpXVFRva2VuIiwgInJlYWxtIjogIi9jdXN0b21lciIsICJhdXRoTGV2ZWwiOiB" +
            "bICIyIiBdLCAiYXVkIjogWyAid2ViYXBpIiBdLCAicmVuIjogMTQzNTU4ODQ3OCwgImp0aSI6ICJlYWFmOWU0YS02Y2MwLTRlNWEtYTExYS1iN" +
            "zlkZmM5YzMxNmEiLCAiaW1zaSI6ICIyNTAxMTAxMDAwMTQ0OCIsICJhdGgiOiAxNDM1NTg4NDE4IH0.0_riT-Ez1rFiXwfANMwnr5eS5UiE-p" +
            "7yODRk1Zu785s";

    public static final long DEFAULT_TIMEOUT = 0;
    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;

    private RooxAuthenticationAuthorizationLibrary aal;
    private Cache<YotaPrincipalKey, YotaPrincipal> principalsCache;
    private SsoAuthenticationClient mockSsoAuthenticationClient;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        mockSsoAuthenticationClient = mock(SsoAuthenticationClient.class);
        principalsCache = mock(Cache.class);
        aal = new RooxAuthenticationAuthorizationLibrary(null, null, mockSsoAuthenticationClient, null, null, null, null, null, AuthorizationType.SSO_TOKEN);
    }

    @Test
    public void renew_principal_correctly() {
        YotaPrincipal oldPrincipal = mock(YotaPrincipal.class);

        when(oldPrincipal.getJwtToken())
                .thenReturn(TOKEN_FOR_229_213_38_0);
        Map<String,Object> params = new HashMap<>();
        params.put(SsoAuthenticationClient.JWT_PARAM_NAME, TOKEN_FOR_229_213_38_0);
        params.put(SsoAuthenticationClient.UPDATE_LIFE_TIME_PARAM, true);
        when(mockSsoAuthenticationClient.authenticate(params))
                .thenReturn(TOKEN_FOR_229_213_38_1);

        YotaPrincipal newPrincipal = aal.renew(oldPrincipal, true, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertNotNull(newPrincipal);
        assertTrue(oldPrincipal != newPrincipal);
    }
}
