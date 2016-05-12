package com.rooxteam.sso.aal;

import lombok.Getter;

public enum AuthParamType {
    IP("ip"), JWT("jwt"), CLIENT_IPS("clientIps");

    @Getter
    private String value;

    AuthParamType(String value) {
        this.value = value;
    }

}
