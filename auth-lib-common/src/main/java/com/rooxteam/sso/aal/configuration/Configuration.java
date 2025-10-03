package com.rooxteam.sso.aal.configuration;

import java.util.List;

public interface Configuration {

    String getString(String property, String defaultValue);

    String getString(String property);

    boolean getBoolean(String property, boolean defaultValue);

    int getInt(String property, int defaultValue);

    List getList(String property);

    String[] getStringArray(String tokenInfoAttributesForward);
}
