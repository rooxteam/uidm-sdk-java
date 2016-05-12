package com.sun.identity.authentication;

import com.iplanet.sso.SSOToken;

/**
 * Redefine OpenAM class for creating mock.
 * Original class cannot be mocked because it contains field AuthContextLocal that is not present in client sdk.
 */
public abstract class AuthContext {

    public abstract Status getStatus();

    public enum Status {
        SUCCESS
    }

    public abstract void login(IndexType type, String str, String[] mas);

    public abstract SSOToken getSSOToken();

    public static class IndexType {
        public static final IndexType SERVICE = new IndexType();
    }
}
