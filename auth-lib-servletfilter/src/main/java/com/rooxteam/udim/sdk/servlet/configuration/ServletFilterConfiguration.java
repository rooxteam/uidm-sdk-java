package com.rooxteam.udim.sdk.servlet.configuration;
import java.util.List;

public interface ServletFilterConfiguration extends ServletFilterServiceConfiguration {
    String getString(String property);
    List<String> getList(String property);
}
