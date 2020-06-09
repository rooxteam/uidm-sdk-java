package com.rooxteam.compat;

public final class Objects {

    public static <T> T requireNonNull(T var0) {
        if (var0 == null) {
            throw new NullPointerException();
        } else {
            return var0;
        }
    }

    public static <T> T requireNonNull(T var0, String var1) {
        if (var0 == null) {
            throw new NullPointerException(var1);
        } else {
            return var0;
        }
    }
}
