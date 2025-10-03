package com.rooxteam.sso.aal.realms.impl

import spock.lang.Specification

/**
 * @author sergey.syroezhkin
 * @since 14.08.2025
 */
class AntStylePatternMatcherTest extends Specification {
    def "MatchUrlToPattern"() {
    }

    def "match utl to pattern with realm segment"() {
        when:
        def result = AntStylePatternMatcher.matchUrlToPattern(pattern, url)

        then:
        (result != null ? result.get("realm") : null) == expectedRealm

        where:
        pattern                                      | url                                                           || expectedRealm
        "/v1/realms/{realm}/principals/**"           | "/v1/realms/public/principals"                                || "public"
        "/v1/realms/{realm}/principals/**"           | "/v1/realms/public_users/principals/test"                     || "public_users"
        "/v1/realms/{realm}/principals/**"           | "/v1/realms/emp-123/principals/test/again"                    || "emp-123"
        "/v2/realms/{realm}/**/principals"           | "/v2/realms/public/x/y/z/principals"                          || "public"
        "/v2/realms/{realm}/**/principals"           | "/v2/realms/myrealm/principals"                               || "myrealm"
        "/v3/realms/{realm}/orgs/*"                  | "/v3/realms/testrealm/orgs/abc"                               || "testrealm"
        "/v3/realms/{realm}/orgs/*"                  | "/v3/realms/testrealm/orgs"                                   || null
        "/v4/realms/{realm}/*/orgs"                  | "/v4/realms/public/x/orgs"                                    || "public"
        "/v4/realms/{realm}/*/orgs"                  | "/v4/realms/public/orgs"                                      || null
        "/v5/{realm}/**"                             | "/v5/anyrealm/some/path/here"                                 || "anyrealm"
        "/v5/{realm}/**"                             | "/v5/single"                                                  || "single"
        "/v5/{realm}/*"                              | "/v5/r1/onlyone"                                              || "r1"
        "/v5/{realm}/*"                              | "/v5/r1/two/segments"                                         || null
    }
}
