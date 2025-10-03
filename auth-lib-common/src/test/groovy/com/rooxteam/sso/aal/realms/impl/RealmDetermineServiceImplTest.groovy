package com.rooxteam.sso.aal.realms.impl

import com.rooxteam.sso.aal.request.HttpRequestInfo
import com.rooxteam.sso.aal.request.HttpRequestInfoHolder
import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * @author sergey.syroezhkin
 * @since 04.08.2025
 */
@Slf4j
class RealmDetermineServiceImplTest extends Specification {

    def "Determine realm - no config"() {
        given:
        def sut = RealmDetermineServiceImpl.builder().build()
        def requestInfo = Mock(HttpRequestInfo)
        HttpRequestInfoHolder.setRequestInfo(requestInfo)

        when:
        def result = sut.determine()

        then:
        requestInfo.getRequestURI() >> requestUri

        and:
        result == expected

        where:
        requestUri | expected
        "/api/customers" | "customer"

    }

    def "Determine realm - with config"() {
        given:
        def sut = RealmDetermineServiceImpl.builder()
                .antMatcher("/v1/realms/{realm}/principals/**")
                .antMatcher("/v2/realms/{realm}/**/principals")
                .antMatcher("/v3/realms/{realm}/orgs/*")
                .antMatcher("/v4/realms/{realm}/*/orgs")
                .defaultRealm("employee")
                .build()

        and: "Mocking HttpServletRequest"
        def requestInfo = Mock(HttpRequestInfo)
        HttpRequestInfoHolder.setRequestInfo(requestInfo)
        requestInfo.getServletPath() >> requestUri

        when:
        def result = sut.determine()

        then:
        result == expected

        where:
        requestUri                                                  | expected
        "/api/customers"                                            | "employee"
        "/v1/realms/public_users/principals"                        | "public_users"
        "/v1/realms/public_users/principals/test"                   | "public_users"
        "/v1/realms/public_users/principals/test/again"             | "public_users"
        "/v1/realms/public_users/principals/test/again/and/again"   | "public_users"
        "/v1/realms/public_users/principals/test/again/and/again"   | "public_users"
        "/v1/realms/public_users/principal"                         | "employee"
        "/v1/realms/public_users/principalstest"                    | "employee"
        "/v1/realms/public_USERS-0123456789/principals"             | "public_USERS-0123456789"

    }


    def "compileAntPatternToRegex matches and extracts realm"() {
        given:
        def sut = RealmDetermineServiceImpl.builder().antMatcher(pattern).build()

        and: "Mocking HttpServletRequest"
        def requestInfo = Mock(HttpRequestInfo)
        HttpRequestInfoHolder.setRequestInfo(requestInfo)
        requestInfo.getServletPath() >> uri

        when:
        def result = sut.determine()

        then:
        result == expectedRealm

        where:
        pattern                                      | uri                                                      | expectedRealm
        "/v1/realms/{realm}/principals/**"           | "/v1/realms/employee/principals"                         | "employee"
        "/v1/realms/{realm}/principals/**"           | "/v1/realms/employee/principals/"                        | "employee"
        "/v1/realms/{realm}/principals/**"           | "/v1/realms/public_default/principals"                   | "public_default"
        "/v1/realms/{realm}/principals/**"           | "/v1/realms/public-default/principals"                   | "public-default"
        "/v1/realms/{realm}/principals/**"           | "/v1/realms/public-DEFAULT_0123456789/principals"        | "public-DEFAULT_0123456789"
        "/v1/realms/{realm}/principals/**"           | "/v1/realms/employee/principals/test"                    | "employee"
        "/v1/realms/{realm}/principals/**"           | "/v1/realms/employee/principals/test/again"              | "employee"
        "/v2/realms/{realm}/**/principals"           | "/v2/realms/admin/something/more/principals"             | "admin"
        "/v3/realms/{realm}/orgs/*"                  | "/v3/realms/test_org/orgs/abc"                           | "test_org"
        "/v4/realms/{realm}/*/orgs"                  | "/v4/realms/dev_env/xyz/orgs"                            | "dev_env"
    }


    def "check very very long URL"() {
        given: "Ant pattern and compiled regex"
        def antPattern = "/v1/realms/{realm}/principals/**"
        def sut = RealmDetermineServiceImpl.builder().antMatcher(antPattern).build()

        and: "A very long URL (~1 MB)"
        def realm = "employee"
        def hugePathSegment = "a" * 1_000_000
        def longUrl = "/v1/realms/${realm}/principals/${hugePathSegment}"

        and: "Mocking HttpServletRequest"
        def requestInfo = Mock(HttpRequestInfo)
        HttpRequestInfoHolder.setRequestInfo(requestInfo)
        requestInfo.getServletPath() >> longUrl

        when: "Matching the long URL"
        def start = System.nanoTime()
        def result = sut.determine()
        def elapsedMs = (System.nanoTime() - start) / 1_000_000

        then: "Match is correct and fast"
        log.info("Elapsed time: {} ms", elapsedMs)
        realm == result
        elapsedMs < 200
    }
}
