package com.rooxteam.udim.sdk.servlet.filter;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.udim.sdk.servlet.configuration.ConfigValues;
import com.rooxteam.udim.sdk.servlet.configuration.ServletFilterConfiguration;
import com.rooxteam.udim.sdk.servlet.dto.PrincipalProperties;
import com.rooxteam.udim.sdk.servlet.exceptions.InvalidForwardedPropertiesConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

class ServletFilterHttpRequestWrapper extends HttpServletRequestWrapper {
    private String principal;
    private Set<String> roles;
    private Map<String, Collection<String>> virtualHeaders;

    private Collection<String> createCollection(Object object) {
        if (object instanceof Collection) {
           return (Collection<String>) object;
        } else if (object instanceof String) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(object.toString());
            return arrayList;
        }
        return null;
    }

    private Collection<String> createCollection(Enumeration<String> strings) {
        ArrayList<String> arrayList = new ArrayList<>();
        while (strings.hasMoreElements()) {
            arrayList.add(strings.nextElement());
        }
        return Collections.unmodifiableList(arrayList);
    }


    private void putIntoHeader(String key, Collection<String> value) {
        if (key != null && value != null) {
            this.virtualHeaders.put(key, value);
        }
    }

    ServletFilterHttpRequestWrapper(HttpServletRequest request,
                                    Principal principal,
                                    ServletFilterConfiguration config) {
        super(request);
        Objects.requireNonNull(principal);
        Objects.requireNonNull(config);
        this.principal = (String) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, PrincipalProperties.prn.name());
        Object uncastRoles = principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, PrincipalProperties.roles.name());
        this.roles = uncastRoles != null
                ? Collections.unmodifiableSet(new TreeSet<>((List<String>) uncastRoles))
                : Collections.unmodifiableSet(new TreeSet<>());

        this.virtualHeaders = new TreeMap<>();
        List<String> properties = config.getList(ConfigValues.PROPERTIES_KEY);
        List<String> headers = config.getList(ConfigValues.HEADER_NAMES_OF_PROPERTIES_KEY);
        if (properties.size() != headers.size()) {
            throw new InvalidForwardedPropertiesConfiguration();
        }
        Iterator<String> propIter = properties.iterator();
        Iterator<String> headerIter = headers.iterator();
        while (propIter.hasNext()) {
            String prop = propIter.next();
            String header = headerIter.next();
            Object propValue = principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, prop);
            putIntoHeader(header, createCollection(propValue));
        }

        Enumeration<String> iter = request.getHeaderNames();
        while (iter.hasMoreElements()) {
            String header = iter.nextElement();
            this.virtualHeaders.put(header, createCollection(request.getHeaders(header)));
        }
    }

    @Override
    public String getRemoteUser() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }

    @Override
    public String getHeader(String header) {
        if (getHeaders(header) != null && getHeaders(header).hasMoreElements()) {
            return getHeaders(header).nextElement();
        }
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String header) {
        return Collections.enumeration(virtualHeaders.get(header));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(virtualHeaders.keySet());
    }
}
