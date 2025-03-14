package com.rooxteam.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class HttpHelper {

    private static final Charset REQUEST_BODY_CHARSET = StandardCharsets.UTF_8;
    private static final Charset REQUEST_PARAMS_CHARSET = StandardCharsets.UTF_8;
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String X_WWW_FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private HttpHelper() {
    }

    public static HttpPost getHttpPostWithEntity(String url, List<NameValuePair> params) throws UnsupportedEncodingException {
        return buildPost(new HttpPost(url), params);
    }

    public static HttpPost getHttpPostWithEntity(URI uri, List<NameValuePair> params) throws UnsupportedEncodingException {
        return buildPost(new HttpPost(uri), params);
    }

    public static HttpPost getHttpPostWithJsonBody(String url, String body) {
        HttpPost post = new HttpPost(url);
        restPrepare(post);
        post.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE));
        post.setEntity(new StringEntity(body, REQUEST_BODY_CHARSET));
        return post;
    }

    public static HttpPost getHttpPost(String url, List<NameValuePair> params) {
        HttpPost post = new HttpPost(url + uniformURLParams(params));
        restPrepare(post);
        return post;
    }

    public static HttpGet getHttpGet(String url, List<NameValuePair> params) {
        HttpGet get = new HttpGet(url + uniformURLParams(params));
        restPrepare(get);
        return get;
    }

    private static void restPrepare(HttpRequest request) {
        request.addHeader(new BasicHeader(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE));
    }

    private static String uniformURLParams(List<NameValuePair> params) {
        return "?" + URLEncodedUtils.format(params, REQUEST_PARAMS_CHARSET);
    }

    private static HttpPost buildPost(HttpPost post, List<NameValuePair> params) throws UnsupportedEncodingException {
        restPrepare(post);
        post.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, X_WWW_FORM_CONTENT_TYPE));
        post.setEntity(new UrlEncodedFormEntity(params, REQUEST_BODY_CHARSET));
        return post;
    }
}
