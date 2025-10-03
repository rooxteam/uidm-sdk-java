package com.rooxteam.uidm.sdk.spring.configuration;

import com.rooxteam.sso.aal.ConfigKeys;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.Assert.assertEquals;

public class SpringEnvironmentRooXUidmOpinionatedConfigurationTest {
    private MockEnvironment environment;
    private SpringEnvironmentRooXUidmOpinionatedConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        environment = new MockEnvironment();
        environment.setProperty(ConfigKeys.SSO_URL, "https://localhost:8443/sso");
        configuration = new SpringEnvironmentRooXUidmOpinionatedConfiguration(environment);
    }

    @Test
    public void getTokenValidationEndpointDefaultRealm() {
        assertEquals("https://localhost:8443/sso/realms/customer/as/oauth2/idp/tokeninfo", configuration.getTokenValidationEndpoint().toString());
    }

    @Test
    public void getTokenValidationEndpointConfiguredRealmWithSlash() {
        environment.setProperty(ConfigKeys.REALM, "/employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/tokeninfo", configuration.getTokenValidationEndpoint().toString());
    }

    @Test
    public void getTokenValidationEndpointConfiguredRealmWithOutSlash() {
        environment.setProperty(ConfigKeys.REALM, "employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/tokeninfo", configuration.getTokenValidationEndpoint().toString());
    }

    @Test
    public void getTokenValidationEndpointConfiguredPath() {
        environment.setProperty(ConfigKeys.REALM, "employee");
        environment.setProperty(ConfigKeys.TOKEN_INFO_PATH, "/my/{realm}/tokeninfo");
        assertEquals("https://localhost:8443/sso/my/employee/tokeninfo", configuration.getTokenValidationEndpoint().toString());
    }

     @Test
    public void getTokenValidationEndpointConfiguredPathNoPlaceholder() {
        environment.setProperty(ConfigKeys.REALM, "employee");
        environment.setProperty(ConfigKeys.TOKEN_INFO_PATH, "/my/tokeninfo");
        assertEquals("https://localhost:8443/sso/my/tokeninfo", configuration.getTokenValidationEndpoint().toString());
    }

    @Test
    public void getAccessTokenEndpointDefaultRealm() {
        assertEquals("https://localhost:8443/sso/realms/customer/as/oauth2/idp/access_token", configuration.getAccessTokenEndpoint().toString());
    }

    @Test
    public void getAccessTokenEndpointConfiguredRealmWithSlash() {
        environment.setProperty(ConfigKeys.REALM, "/employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/access_token", configuration.getAccessTokenEndpoint().toString());
    }

    @Test
    public void getAccessTokenEndpointEndpointConfiguredRealmWithOutSlash() {
        environment.setProperty(ConfigKeys.REALM, "employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/access_token", configuration.getAccessTokenEndpoint().toString());
    }

    @Test
    public void getTokenExchangeEndpointDefaultRealm() {
        assertEquals("https://localhost:8443/sso/realms/customer/as/oauth2/idp/access_token", configuration.getTokenExchangeEndpoint().toString());
    }

    @Test
    public void getTokenExchangeEndpointConfiguredRealmWithSlash() {
        environment.setProperty(ConfigKeys.REALM, "/employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/access_token", configuration.getTokenExchangeEndpoint().toString());
    }

    @Test
    public void getTokenExchangeEndpointEndpointConfiguredRealmWithOutSlash() {
        environment.setProperty(ConfigKeys.REALM, "employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/access_token", configuration.getTokenExchangeEndpoint().toString());
    }
}