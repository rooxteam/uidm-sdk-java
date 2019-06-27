package com.rooxteam.udim.sdk.servlet.testing;

import com.rooxteam.udim.sdk.servlet.configuration.ServletFilterConfiguration;
import com.rooxteam.udim.sdk.servlet.filter.ServletFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class FilterForTesting extends ServletFilter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletFilterConfiguration servletFilterConfiguration = new ServletFilterConfigurationForTesting();
        this.finishInit(servletFilterConfiguration);
    }

}
