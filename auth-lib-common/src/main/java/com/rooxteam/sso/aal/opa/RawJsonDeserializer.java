package com.rooxteam.sso.aal.opa;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class RawJsonDeserializer extends JsonDeserializer {

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        long begin = jp.getCurrentLocation().getCharOffset();
        jp.skipChildren();
        long end = jp.getCurrentLocation().getCharOffset();

        String json = jp.getCurrentLocation().getSourceRef().toString();
        return json.substring((int) begin - 1, (int) end);
    }
}
