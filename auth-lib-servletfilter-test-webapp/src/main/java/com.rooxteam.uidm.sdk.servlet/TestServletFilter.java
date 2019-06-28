package com.rooxteam.uidm.sdk.servlet;

import com.rooxteam.udim.sdk.servlet.filter.ServletAuthFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class TestServletFilter extends ServletAuthFilter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        finishInit(new ServletFilterConfigurationForTesting());
    }

}
