package com.rooxteam.uidm.sdk.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author sergey.syroezhkin
 * @since 19.09.2025
 */
public class ScopeDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.readValueAsTree();
        if (node.isArray()) {
            // Legacy UIDM scope в виде списка строк
            return Arrays.asList(jsonParser.getCodec().treeToValue(node, String[].class));
        } else if (node.isTextual()) {
            // Для случая когда scope приходит в RFC-complain формате: строка с разделителем пробел.
            String text = node.asText().trim();
            return text.isEmpty() ? Collections.emptyList() : Arrays.asList(text.split("\\s+"));
        }
        return Collections.emptyList();
    }

}
