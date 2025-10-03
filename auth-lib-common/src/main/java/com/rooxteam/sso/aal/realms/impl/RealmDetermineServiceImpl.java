package com.rooxteam.sso.aal.realms.impl;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.realms.RealmDetermineService;
import com.rooxteam.sso.aal.request.HttpRequestInfo;
import com.rooxteam.sso.aal.request.HttpRequestInfoHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sergey.syroezhkin
 * @since 04.08.2025
 */
public class RealmDetermineServiceImpl implements RealmDetermineService {

    private final String defaultRealm;
    private final List<String> patterns;
    private RealmDetermineServiceImpl(List<String> patterns, String defaultRealm) {
        this.defaultRealm = defaultRealm;
        this.patterns = patterns;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String determine() {
        HttpRequestInfo httpRequestInfo = HttpRequestInfoHolder.getRequestInfo();
        if (httpRequestInfo != null) {
            String uri = httpRequestInfo.getServletPath();
            for (String entry : patterns) {
                Map<String, String> matchResult = AntStylePatternMatcher.matchUrlToPattern(entry, uri);
                if (matchResult != null) {
                    return matchResult.get("realm");
                }
            }
        }
        return defaultRealm;
    }

    public static class Builder {

        private final List<String> patterns = new ArrayList<>();
        private String defaultRealm;

        public Builder() {
            this.defaultRealm = ConfigKeys.REALM_DEFAULT;
        }

        public Builder defaultRealmFromConfiguration(Configuration configuration) {
            this.defaultRealm = configuration.getString(ConfigKeys.REALM, ConfigKeys.REALM_DEFAULT);
            return this;
        }

        public Builder antMatcher(String antPatternWithRealm) {
            patterns.add(antPatternWithRealm);
            return this;
        }

        public Builder defaultRealm(String defaultRealm) {
            this.defaultRealm = defaultRealm;
            return this;
        }

        public RealmDetermineService build() {
            return new RealmDetermineServiceImpl(patterns, defaultRealm);
        }
    }

}
