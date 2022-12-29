package com.rooxteam.sso.aal.otp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Обертка буфферизироованный контейнер для оригианльного HTTP-запроса, представляя собой комбинация тела запроса и
 * заголовков. В отличие от обычных реализаций {@link HttpInputMessage} допускается  многократное обращение ко входному
 * потоку {@link #getBody()}.
 */
@AllArgsConstructor
public class RequestWrapper implements HttpInputMessage {

    private final byte[] body;
    @Getter
    private final HttpHeaders headers;

    public InputStream getBody() {
        return body != null ? new ByteArrayInputStream(body) : StreamUtils.emptyInput();
    }
}
