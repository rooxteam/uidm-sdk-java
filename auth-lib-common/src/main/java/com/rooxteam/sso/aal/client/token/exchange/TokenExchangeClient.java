package com.rooxteam.sso.aal.client.token.exchange;

import com.rooxteam.sso.aal.client.model.TokenExchangeRequest;
import com.rooxteam.sso.aal.client.token.exchange.dto.TokenResponse;

import java.io.Closeable;
import java.util.Map;

public interface TokenExchangeClient extends Closeable {
    TokenResponse exchangeToken(TokenExchangeRequest tokenExchangeRequest,
                                Map<String, String> extraArgs) throws Exception;
}
