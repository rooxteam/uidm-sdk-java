package com.rooxteam.uidm.sdk.spring.authentication

import com.rooxteam.sso.aal.exception.AuthenticationException
import spock.lang.Specification

class RealmValidatorTest extends Specification {

    def "validate  realm format"() {
        when:
        RealmValidator.validateRealm(realm, authenticationState)

        then:
        def e = thrown(AuthenticationException)
        e.message == "Realm not valid"

        where:
        realm                | authenticationState
        "customer?1"         | new AuthenticationState()
        "customer a"         | new AuthenticationState()
        "customer   a^?>"    | new AuthenticationState()
        "customer" * 8 + "1" | new AuthenticationState() //65 symbols
    }

    def "validate customer realm successfully"() {
        given:
        def realm = "customer"
        def authenticationState = new AuthenticationState()
        authenticationState.setRealm(realm)

        when:
        RealmValidator.validateRealm(realm, authenticationState)

        then:
        notThrown(AuthenticationException)
    }

    def "validate customer realm not successfully"() {
        given:
        def realm = "customer"
        def authenticationState = new AuthenticationState()

        when:
        RealmValidator.validateRealm(realm, authenticationState)

        then:
        def e = thrown(AuthenticationException)
        e.message == "Realm not matched"
    }
}
