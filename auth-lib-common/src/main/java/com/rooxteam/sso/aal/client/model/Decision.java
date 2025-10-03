package com.rooxteam.sso.aal.client.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Decision {

    Permit(true),
    Deny(false);

    private final boolean positive;

    public static Decision fromAllow(boolean allow) {
        return allow ? Permit : Deny;
    }
}
