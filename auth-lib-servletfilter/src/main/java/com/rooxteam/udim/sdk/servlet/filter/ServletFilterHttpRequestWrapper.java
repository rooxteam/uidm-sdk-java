package com.rooxteam.udim.sdk.servlet.filter;

import com.rooxteam.udim.sdk.servlet.configuration.Configuration;
import com.rooxteam.udim.sdk.servlet.dto.TokenInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
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
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(object.toString());
        return arrayList;
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
                                    TokenInfo tokenInfo,
                                    Configuration config) {
        super(request);
        Objects.requireNonNull(tokenInfo);
        Objects.requireNonNull(config);
        this.principal = tokenInfo.getPrincipal();
        this.roles = tokenInfo.getRoles() != null
                ? Collections.unmodifiableSet(new TreeSet<>(tokenInfo.getRoles()))
                : Collections.unmodifiableSet(new TreeSet<>());

        this.virtualHeaders = new TreeMap<>();
        putIntoHeader(config.getPrincipalHeader(), createCollection(principal));
        putIntoHeader(config.getRolesHeader(), roles);
        putIntoHeader(config.getAuthLevelHeader(), createCollection(tokenInfo.getAuthLevel()));
        putIntoHeader(config.getExpiresInHeader(), createCollection(tokenInfo.getExpiresIn()));

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
