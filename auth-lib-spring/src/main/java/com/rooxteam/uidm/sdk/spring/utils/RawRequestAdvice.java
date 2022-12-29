package com.rooxteam.uidm.sdk.spring.utils;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.otp.RequestWrapper;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

@RestControllerAdvice
@AllArgsConstructor
public class RawRequestAdvice extends RequestBodyAdviceAdapter {

    private final RequestData data;
    private final Configuration config;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(RawRequest.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        boolean enabled = config.getBoolean(ConfigKeys.PASS_IMITATION_PARAMS, false);
        if (!enabled) {
            return inputMessage;
        }

        try (InputStream inputStream = inputMessage.getBody()) {
            byte[] body = StreamUtils.copyToByteArray(inputStream);
            data.setWrapper(new RequestWrapper(body, inputMessage.getHeaders()));
            return new RequestWrapper(body, inputMessage.getHeaders());
        }
    }
}
