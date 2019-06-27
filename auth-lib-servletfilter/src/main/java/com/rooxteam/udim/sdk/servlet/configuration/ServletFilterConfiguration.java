package com.rooxteam.udim.sdk.servlet.configuration;

import com.rooxteam.sso.aal.configuration.Configuration;

public interface ServletFilterConfiguration extends Configuration, ServletFilterServiceConfiguration {
    String getString(String property);
}
