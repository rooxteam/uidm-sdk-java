package com.rooxteam.uidm.sdk.servlet.filter;

import com.rooxteam.compat.Objects;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.uidm.sdk.servlet.dto.PrincipalClaims;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ServletAuthFilterHttpRequestWrapper extends HttpServletRequestWrapper {
    private String principal;
    private Set<String> roles;
    private Map<String, Object> attributes;
    private Map<String, Collection<String>> headers;

    private Collection<String> getCollection(Object object) {
        if (object instanceof Collection) {
            Collection<Object> objectCollection = (Collection<Object>) object;
            List<String> ret = new ArrayList<String>();
            for (Object o : objectCollection) {
                if (o != null) {
                    ret.add(o.toString());
                }
            }
            return ret;
        } else if (object instanceof String) {
            return Collections.singletonList((String) object);
        }
        return null;
    }

    private Collection<String> createCollection(Enumeration<String> strings) {
        ArrayList<String> arrayList = new ArrayList<String>();
        while (strings.hasMoreElements()) {
            arrayList.add(strings.nextElement());
        }
        return Collections.unmodifiableList(arrayList);
    }


    private void putIntoHeader(String key,
                               Collection<String> value) {
        if (key != null && value != null) {
            this.headers.put(key, value);
        }
    }

    ServletAuthFilterHttpRequestWrapper(HttpServletRequest request,
                                        Principal principal,
                                        Map<String, String> headerNamesOfTokenClaims,
                                        Map<String, String> attributeNamesOfTokenClaims) {
        super(request);
        Objects.requireNonNull(principal);
        Objects.requireNonNull(attributeNamesOfTokenClaims);
        Objects.requireNonNull(headerNamesOfTokenClaims);
        this.principal = (String) principal.getProperty(PrincipalClaims.prn.name());
        Object uncastRoles = principal.getProperty(PrincipalClaims.roles.name());
        this.roles = uncastRoles != null
                ? Collections.unmodifiableSet(new TreeSet<String>((List<String>) uncastRoles))
                : Collections.unmodifiableSet(new TreeSet<String>());

        this.headers = new TreeMap();
        this.attributes = new TreeMap();

        for (Map.Entry<String, String> entry : headerNamesOfTokenClaims.entrySet()) {
            Object propValue = principal.getProperty(entry.getKey());
            putIntoHeader(entry.getValue(), getCollection(propValue));
        }

        for (Map.Entry<String, String> entry : attributeNamesOfTokenClaims.entrySet()) {
            Object propValue = principal.getProperty(entry.getKey());
            this.attributes.put(entry.getValue(), propValue);
        }

        Enumeration<String> headerNamesIter = request.getHeaderNames();
        while (headerNamesIter != null && headerNamesIter.hasMoreElements()) {
            String header = headerNamesIter.nextElement();
            this.headers.put(header, createCollection(request.getHeaders(header)));
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
        if (header != null && headers.get(header) != null) {
            return Collections.enumeration(headers.get(header));
        } else {
            return null;
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
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
    public void setAttribute(String var1,
                             Object var2) {
        attributes.put(var1, var2);
    }

    @Override
    public void removeAttribute(String var1) {
        attributes.remove(var1);
    }
}
