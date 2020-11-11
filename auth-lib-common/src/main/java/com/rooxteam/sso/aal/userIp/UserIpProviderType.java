package com.rooxteam.sso.aal.userIp;

/**
 * @author sergey.syroezhkin
 * @since 11.11.2020
 */
enum UserIpProviderType {

    NO_PROVIDER,
    REQUEST,
    HEADER;

    static UserIpProviderType of(String value) {
        for (UserIpProviderType v: values()) {
            if (v.name().equalsIgnoreCase(value)) {
                return v;
            }
        }
        return NO_PROVIDER;
    }

}
