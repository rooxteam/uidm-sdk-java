package com.rooxteam.util;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;
import java.util.List;

public final class HttpHelper {

    private static final String REQUEST_BODY_CHARSET = "UTF-8";
    private static final String REQUEST_PARAMS_CHARSET = "UTF-8";
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String X_WWW_FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private HttpHelper() {
    }

    public static HttpPost getHttpPostWithEntity(String url, List<NameValuePair> params) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(url);
        restPrepare(post);
        post.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, X_WWW_FORM_CONTENT_TYPE));
        post.setEntity(new UrlEncodedFormEntity(params, REQUEST_BODY_CHARSET));
        return post;
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
}
