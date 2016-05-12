package com.rooxteam.sso.aal.client;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;
import java.util.List;

class HttpHelper {

    private static final String REQUEST_BODY_CHARSET = "UTF-8";
    private static final String REQUEST_PARAMS_CHARSET = "UTF-8";

    static HttpPost getHttpPostWithEntity(String url, List<NameValuePair> params) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(url);
        restPrepare(post);
        post.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded"));
        post.setEntity(new UrlEncodedFormEntity(params, REQUEST_BODY_CHARSET));
        return post;
    }

    static HttpPost getHttpPostWithJsonBody(String url, String body) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(url);
        restPrepare(post);
        post.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
        post.setEntity(new StringEntity(body));
        return post;
    }

    static HttpGet getHttpGet(String url, List<NameValuePair> params) throws UnsupportedEncodingException {
        HttpGet get = new HttpGet(url + uniformURLParams(params));
        restPrepare(get);
        return get;
    }

    private static void restPrepare(HttpRequest request) {
        request.addHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
    }

    private static String uniformURLParams(List<NameValuePair> params) {
        return "?" + URLEncodedUtils.format(params, REQUEST_PARAMS_CHARSET);
    }
}
