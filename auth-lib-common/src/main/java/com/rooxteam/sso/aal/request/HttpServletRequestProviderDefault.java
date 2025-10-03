package com.rooxteam.sso.aal.request;

import javax.servlet.http.HttpServletRequest;

/**
 * Retrieves current HttpServletRequest from {@see com.rooxteam.sso.aal.request.HttpServletRequestHolder}
 * Setup {@see com.rooxteam.uidm.sdk.servlet.filter.HttpServletRequestHolderFilter} or put current request manually:
 * <code>
 *         try {
 *             HttpServletRequestHolder.setRequest(request);
 *             ......
 *         } finally {
 *             HttpServletRequestHolder.clear();
 *         }
 * </code>
 */
public class HttpServletRequestProviderDefault implements HttpServletRequestProvider {

    @Override
    public HttpServletRequest getHttpServletRequest() {
        return HttpServletRequestHolder.getRequest();
    }

}
