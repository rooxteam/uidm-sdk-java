package com.rooxteam.uidm.sdk.hmac;

import com.rooxteam.sso.aal.ConfigKeys;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
public class CachedRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(ConfigKeys.REQUEST_SIGNATURE_HEADER);
        if (header == null) {
            filterChain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper(request);
        filterChain.doFilter(cachedRequest, response);
    }
}
