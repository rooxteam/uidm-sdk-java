package com.rooxteam.sso.aal.client.configuration;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.configuration.ConfigurationBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ClientConfigurationImplTest {
    private Map<String, Object> configMap;
    private Configuration config;
    private ClientConfigurationImpl configuration;

    @Before
    public void setUp() throws Exception {
        configMap = new HashMap<>();
        configMap.put(ConfigKeys.SSO_URL, "https://localhost:8443/sso");
        config = ConfigurationBuilder.fromMap(configMap) ;
        configuration = new ClientConfigurationImpl(config);
    }


    @Test
    public void getTokenValidationEndpointDefaultRealm() {
        assertEquals("https://localhost:8443/sso/realms/customer/as/oauth2/idp/tokeninfo", configuration.getTokenValidationEndpoint().toString());
    }

    @Test
    public void getTokenValidationEndpointConfiguredRealmWithSlash() {
        configMap.put(ConfigKeys.REALM, "/employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/tokeninfo", configuration.getTokenValidationEndpoint().toString());
    }

    @Test
    public void getTokenValidationEndpointConfiguredRealmWithOutSlash() {
        configMap.put(ConfigKeys.REALM, "employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/tokeninfo", configuration.getTokenValidationEndpoint().toString());
    }

    @Test
    public void getTokenValidationEndpointConfiguredPath() {
        configMap.put(ConfigKeys.REALM, "employee");
        configMap.put(ConfigKeys.TOKEN_INFO_PATH, "/my/{realm}/tokeninfo");
        assertEquals("https://localhost:8443/sso/my/employee/tokeninfo", configuration.getTokenValidationEndpoint().toString());
    }

    @Test
    public void getTokenValidationEndpointConfiguredPathNoPlaceholder() {
        configMap.put(ConfigKeys.REALM, "employee");
        configMap.put(ConfigKeys.TOKEN_INFO_PATH, "/my/tokeninfo");
        assertEquals("https://localhost:8443/sso/my/tokeninfo", configuration.getTokenValidationEndpoint().toString());
    }

    @Test
    public void getAccessTokenEndpointDefaultRealm() {
        assertEquals("https://localhost:8443/sso/realms/customer/as/oauth2/idp/access_token", configuration.getAccessTokenEndpoint().toString());
    }

    @Test
    public void getAccessTokenEndpointConfiguredRealmWithSlash() {
        configMap.put(ConfigKeys.REALM, "/employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/access_token", configuration.getAccessTokenEndpoint().toString());
    }

    @Test
    public void getAccessTokenEndpointEndpointConfiguredRealmWithOutSlash() {
        configMap.put(ConfigKeys.REALM, "employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/access_token", configuration.getAccessTokenEndpoint().toString());
    }

    @Test
    public void getTokenExchangeEndpointDefaultRealm() {
        assertEquals("https://localhost:8443/sso/realms/customer/as/oauth2/idp/access_token", configuration.getTokenExchangeEndpoint().toString());
    }

    @Test
    public void getTokenExchangeEndpointConfiguredRealmWithSlash() {
        configMap.put(ConfigKeys.REALM, "/employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/access_token", configuration.getTokenExchangeEndpoint().toString());
    }

    @Test
    public void getTokenExchangeEndpointEndpointConfiguredRealmWithOutSlash() {
        configMap.put(ConfigKeys.REALM, "employee");
        assertEquals("https://localhost:8443/sso/realms/employee/as/oauth2/idp/access_token", configuration.getTokenExchangeEndpoint().toString());
    }
}