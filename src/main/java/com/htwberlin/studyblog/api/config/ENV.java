package com.htwberlin.studyblog.api.config;

public final class ENV {
    public static String getJWTSecret() {
        return System.getenv().get("JWT_SECRET");
    }
}
