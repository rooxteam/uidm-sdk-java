package com.rooxteam.udim.sdk.servlet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rooxteam.udim.sdk.servlet.dto.TokenInfo;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;

import java.io.IOException;
import java.io.StringWriter;

class ValidateServiceResponseHandler extends AbstractResponseHandler<TokenInfo> {

    private ObjectMapper objectMapper;

    ValidateServiceResponseHandler(ObjectMapper mapper) {
        this.objectMapper = mapper;
    }

    @Override
    public TokenInfo handleEntity(HttpEntity entity) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(entity.getContent(), writer, Charsets.UTF_8);
        String theString = writer.toString();
        return objectMapper.readValue(theString, TokenInfo.class);
    }
}
