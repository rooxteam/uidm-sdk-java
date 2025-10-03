package com.rooxteam.sso.aal.request;

/**
 * Retrieves current http request info from {@see com.rooxteam.sso.aal.request.HttpRequestInfoHolder}
 * Setup {@see com.rooxteam.request.HttpServletRequestHolderFilter} or put current request manually:
 * <code>
 *         try {
 *             HttpRequestInfoHolder.setRequest(request);
 *             ......
 *         } finally {
 *             HttpRequestInfoHolder.clear();
 *         }
 * </code>
 */
public class HttpRequestInfoProviderDefault implements HttpRequestInfoProvider {

    @Override
    public HttpRequestInfo getHttpRequestInfo() {
        return HttpRequestInfoHolder.getRequestInfo();
    }

}
