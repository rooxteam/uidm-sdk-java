package com.rooxteam.uidm.sdk.utils.json

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import spock.lang.Specification

/**
 * @author sergey.syroezhkin
 * @since 19.09.2025
 */
class ScopeDeserializerTest extends Specification {


    static class TestModel {
        @JsonProperty("scope")
        @JsonDeserialize(using = ScopeDeserializer)
        List<String> scope
    }

    def mapper = new ObjectMapper()

    def "deserializing scope"() {
        when:
        def result = mapper.readValue(json, TestModel)

        then:
        result.scope == expected

        where:
        json                                     | expected
        '{ "scope": ["one", "two"] }'            | ["one", "two"]
        '{ "scope": ["single"] }'                | ["single"]
        '{ "scope": [] }'                        | []
        '{ "scope": "one two" }'                 | ["one", "two"]
        '{ "scope": "single" }'                  | ["single"]
        '{ "scope": "   " }'                     | []
        '{ "scope": "" }'                        | []
        '{ "scope": " one   two   three " }'     | ["one", "two", "three"]
    }
}
