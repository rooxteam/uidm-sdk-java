package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;

import java.io.IOException;
import java.net.URL;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * DefaultResourceRetriever wrapper with logging
 */
public class JwksResourceRetriever extends DefaultResourceRetriever {

    public JwksResourceRetriever(final int connectTimeout, final int readTimeout) {
        super(connectTimeout, readTimeout, 0);
        LOG.debugv("JWKS retriever initialization. connectTimeout: {0}, readTimeout: {1}", connectTimeout, readTimeout);
    }

    @Override
    public Resource retrieveResource(final URL url) throws IOException {
        LOG.debugv("JWKS retrieving. URL: {0}", url);
        return super.retrieveResource(url);
    }

}
