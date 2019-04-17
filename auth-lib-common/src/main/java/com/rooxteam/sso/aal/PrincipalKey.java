package com.rooxteam.sso.aal;

class PrincipalKey implements AalCacheKey {
    AuthParamType type;
    String ip;

    String clientIps;

    public PrincipalKey(AuthParamType authParamType, String ip, String clientIps) {
        type = authParamType;
        this.ip = ip;
        this.clientIps = clientIps;
    }

    public PrincipalKey(AuthParamType authParamType, String ip) {
        this(authParamType, ip, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrincipalKey that = (PrincipalKey) o;

        if (clientIps != null ? !clientIps.equals(that.clientIps) : that.clientIps != null) return false;
        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (clientIps != null ? clientIps.hashCode() : 0);
        return result;
    }
}