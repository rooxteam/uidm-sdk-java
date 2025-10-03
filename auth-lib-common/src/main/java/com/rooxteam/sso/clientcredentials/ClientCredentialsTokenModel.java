package com.rooxteam.sso.clientcredentials;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public final class ClientCredentialsTokenModel {

    private final String value;

    private final LocalDateTime issueDate;

    private final LocalDateTime expiresIn;
}
