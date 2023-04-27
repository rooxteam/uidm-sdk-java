package com.rooxteam.uidm.sdk.hmac;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.utils.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HMACPayloadBuilder {

    private static final String SOURCE_REQUEST_ATTRIBUTE = "hmacPayload";
    private static final String HMAC_HEADER_PARAM_NAME = "hmacHeader";

    public static final String EOL = "\n";

    public static Map<String, ?> build(Principal principal, HttpServletRequest request) {

        String hmacHeader = request.getHeader(ConfigKeys.REQUEST_SIGNATURE_HEADER);
        if (StringUtils.isEmpty(hmacHeader)) {
            return Collections.emptyMap();
        }

        HMACSignature hmacSignature = HMACRequestHeaderParser.parse(hmacHeader);
        String requestBody = extractRequestBody(request);
        String payload = innerBuild(request, principal, hmacSignature, requestBody);
        Map<String, Object> result = new HashMap<>();
        result.put(HMAC_HEADER_PARAM_NAME, hmacHeader);
        result.put(SOURCE_REQUEST_ATTRIBUTE, payload);
        return result;
    }

    private static String innerBuild(HttpServletRequest request,
                                     Principal principal,
                                     HMACSignature hmacSignature,
                                     String requestEncoded) {

        StringBuilder sb = new StringBuilder();

        // хостнейм API запроса
        String host = request.getServerName();
        appendLine(sb, host);

        // путь API запроса (с открывающим слешом, без знаков # и ?)
        String path = takeApiPath(request);
        appendLine(sb, path);

        // параметры API запроса строкой. Значения параметров должны быть URL-кодированными,
        // если они используют символы, требующие кодирования
        appendLine(sb, request.getQueryString());

        // тип данных. В настоящий момент поддерживается только «application/json». Укажите данную строку в точности.
        String contentType = request.getContentType();
        appendLine(sb, contentType);

        // HTTP-метод запроса апперкейсом, например GET, POST
        String method = request.getMethod().toUpperCase();
        appendLine(sb, method);

        // полезные данные запроса. JSON объект или массив сериализованный стандартными средствами браузера.
        appendLine(sb, requestEncoded);

        // Timestamp
        Long timestamp = hmacSignature.getTimestamp();
        sb.append(timestamp).append(EOL);

        // Principal Id
        String principalId = (String) principal.getProperty("sub");
        appendLine(sb, principalId);

        // Realm
        String realm = (String) principal.getProperty("realm");
        appendLine(sb, realm);

        return sb.toString();
    }

    private static void appendLine(StringBuilder sb, String str) {
        String valueToAdd = str != null ? str : "";
        sb.append(valueToAdd).append(EOL);
    }

    private static String extractRequestBody(HttpServletRequest request) {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            return "";
        }
        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
        byte[] requestData = requestWrapper.getContentAsByteArray();
        if (requestData == null) {
            return "";
        }
        String encoding = Optional.ofNullable(requestWrapper.getCharacterEncoding())
                .orElse(StandardCharsets.UTF_8.displayName());
        try {
            return new String(requestData, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unknown request encoding '" + encoding + "'", e);
        }
    }

    private static String takeApiPath(HttpServletRequest request) {
        return request.getContextPath() + request.getServletPath() +
                (request.getPathInfo() != null ? request.getPathInfo() : "");
    }
}
