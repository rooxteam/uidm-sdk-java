package com.rooxteam.sso.aal;

import com.rooxteam.sso.aal.client.SsoAuthenticationClient;
import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static org.mockito.Mockito.mock;


public class AALInvalidationTest {

    public static final String IP_229_213_38_0 = "229.213.38.0";
    public static final String IP_229_213_38_1 = "229.213.38.1";
    private SsoAuthenticationClient mockSsoAuthenticationClient;

    public static final String TOKEN_FOR_229_213_38_0 = "eyJhbGciOiJIUzI1NiIsImN0eSI6IkpXVCIsInR5cCI6Imp3dCJ9.eyJ0b2tlbk5hbWUiOiJpZF90b2tlbiIsImF6cCI6IndlYmFwaSIsInN1YiI6IjI1MDExMDEwMDAxNDQ4IiwibXNpc2RuIjoiMjUwMTEwMTAwMDE0NDgiLCJpc3MiOiJQY3JmQXV0aGVudGljYXRpb25TZXJ2aWNlIiwidmVyIjoiMS4wIiwiaWF0IjoxNDM1NTY5NTMwLCJleHAiOjE0MzU1Njk1OTAsInRva2VuVHlwZSI6IkpXVFRva2VuIiwicmVhbG0iOiIvY3VzdG9tZXIiLCJhdXRoTGV2ZWwiOlsiMiJdLCJhdWQiOlsid2ViYXBpIl0sInJlbiI6MTQzNTU2OTU5MCwianRpIjoiMmM2MzJkMWEtMjUzNS00MzYxLThlOTAtYmM0OWI0ZTQ5MTJiIiwiaW1zaSI6IjI1MDExMDEwMDAxNDQ4IiwiYXRoIjoxNDM1NTY5NTMwLCJhdXRoVHlwZSI6ImlwIn0.MI_8yce3rudJHL1jLdNpK5pQ790blmJ1hxvEydQxOGw";

    public static final String TOKEN_FOR_229_213_38_1 = "eyJhbGciOiJIUzI1NiIsImN0eSI6IkpXVCIsInR5cCI6Imp3dCJ9.eyJ0b2tlbk5hbWUiOiJpZF90b2tlbiIsImF6cCI6IndlYmFwaSIsInN1YiI6IjI1MDExMDEwMDAxNDQ4IiwibXNpc2RuIjoiMjUwMTEwMTAwMDE0NDgiLCJpc3MiOiJQY3JmQXV0aGVudGljYXRpb25TZXJ2aWNlIiwidmVyIjoiMS4wIiwiaWF0IjoxNDM1NTg4NDE4LCJleHAiOjE0MzU1ODg0NzgsInRva2VuVHlwZSI6IkpXVFRva2VuIiwicmVhbG0iOiIvY3VzdG9tZXIiLCJhdXRoTGV2ZWwiOlsiMiJdLCJhdWQiOlsid2ViYXBpIl0sInJlbiI6MTQzNTU4ODQ3OCwianRpIjoiZWFhZjllNGEtNmNjMC00ZTVhLWExMWEtYjc5ZGZjOWMzMTZhIiwiaW1zaSI6IjI1MDExMDEwMDAxNDQ4IiwiYXRoIjoxNDM1NTg4NDE4LCJhdXRoVHlwZSI6ImlwIn0.mU01fEstruYmvaxF0riHkA66UpNohiNIlSkJJ8t0o_8";

    @Before
    public void setUp() {
        mockSsoAuthenticationClient = mock(SsoAuthenticationClient.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidate_should_throw_illegalArgumentException_when_principal_is_null() {
        Configuration config = new BaseConfiguration();
        config.setProperty("com.rooxteam.aal.jwt.issuer", "TEST_ISSUER");
        AuthenticationAuthorizationLibrary aal = AalFactory.create(ConfigurationBuilder.fromApacheCommonsConfiguration(config));
        Whitebox.setInternalState(aal, "ssoAuthenticationClient", mockSsoAuthenticationClient);
        aal.invalidate(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void invalidate_by_imsi_should_throw_illegalArgumentException_when_principal_is_null() {
        Configuration config = new BaseConfiguration();
        config.setProperty("com.rooxteam.aal.jwt.issuer", "TEST_ISSUER");
        AuthenticationAuthorizationLibrary aal = AalFactory.create(ConfigurationBuilder.fromApacheCommonsConfiguration(config));
        Whitebox.setInternalState(aal, "ssoAuthenticationClient", mockSsoAuthenticationClient);
        aal.invalidateByImsi(null);
    }
}
