package com.rooxteam.request;

import com.rooxteam.sso.aal.request.HttpServletRequestProvider;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Spring implementation of {@see com.rooxteam.sso.aal.request.HttpServletRequestProvider}
 * Retrieves request from {@see org.springframework.web.context.request.RequestContextHolder}
 */
public class HttpServletRequestProviderSpringImpl implements HttpServletRequestProvider {

    @Override
    public HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

}
