package com.rooxteam.uidm.sdk.servlet.filter;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.uidm.sdk.servlet.configuration.FilterConfigKeys;
import com.rooxteam.uidm.sdk.servlet.dto.PrincipalClaims;
import com.rooxteam.uidm.sdk.servlet.exceptions.InvalidForwardedPropertiesConfiguration;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.Arrays;
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

public class ServletFilterHttpRequestWrapper extends HttpServletRequestWrapper {
    public static final String ATTRIBUTE_NAME_PREFIX = "com.rooxteam.uidm.sdk.servlet.filter.attribute.";

    private String principal;
    private Set<String> roles;
    private Map<String, Object> attributes;
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

    private List<String> parseConfigString(String str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        } else {
            return Arrays.asList(str.split(","));
        }
    }

    ServletFilterHttpRequestWrapper(HttpServletRequest request,
                                    Principal principal,
                                    FilterConfig config) {
        super(request);
        Objects.requireNonNull(principal);
        Objects.requireNonNull(config);
        this.principal = (String) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, PrincipalClaims.prn.name());
        Object uncastRoles = principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, PrincipalClaims.roles.name());
        this.roles = uncastRoles != null
                ? Collections.unmodifiableSet(new TreeSet<>((List<String>) uncastRoles))
                : Collections.unmodifiableSet(new TreeSet<>());

        this.virtualHeaders = new TreeMap<>();
        this.attributes = new TreeMap<>();
        List<String> claims = parseConfigString(config.getInitParameter(FilterConfigKeys.CLAIMS_KEY));
        List<String> headersNames = parseConfigString(config.getInitParameter(FilterConfigKeys.HEADER_NAMES_OF_CLAIMS_KEY));
        List<String> attributeNames = parseConfigString(config.getInitParameter(FilterConfigKeys.ATTRIBUTE_NAMES_OF_CLAIMS_KEY));

        if (claims.size() != headersNames.size() || claims.size() != attributeNames.size()) {
            throw new InvalidForwardedPropertiesConfiguration();
        }
        Iterator<String> claimIter = claims.iterator();
        Iterator<String> headerIter = headersNames.iterator();
        Iterator<String> attributeIter = attributeNames.iterator();
        while (claimIter.hasNext()) {
            String prop = claimIter.next();
            String header = headerIter.next();
            String attr = attributeIter.next();
            Object propValue = principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, prop);
            putIntoHeader(header, createCollection(propValue));
            this.attributes.put(ATTRIBUTE_NAME_PREFIX + attr, propValue);
        }

        Enumeration<String> headerNamesIter = request.getHeaderNames();
        while (headerNamesIter != null && headerNamesIter.hasMoreElements()) {
            String header = headerNamesIter.nextElement();
            this.virtualHeaders.put(header, createCollection(request.getHeaders(header)));
        }

        Enumeration<String> attributeNamesIter = request.getAttributeNames();
        while (attributeNamesIter != null && attributeNamesIter.hasMoreElements()) {
            String attribute = attributeNamesIter.nextElement();
            this.attributes.put(attribute, request.getAttribute(attribute));
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
        } else {
            return null;
        }
    }

    @Override
    public Enumeration<String> getHeaders(String header) {
        if (header != null && virtualHeaders.get(header) != null) {
            return Collections.enumeration(virtualHeaders.get(header));
        } else {
            return null;
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(virtualHeaders.keySet());
    }

    @Override
    public Object getAttribute(String var1) {
        return attributes.get(var1);
    }

    @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String var1, Object var2) {
        attributes.put(var1, var2);
    }

    @Override
    public void removeAttribute(String var1) {
        attributes.remove(var1);
    }
}
