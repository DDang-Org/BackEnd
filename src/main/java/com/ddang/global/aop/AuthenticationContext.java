package com.ddang.global.aop;

public class AuthenticationContext {
    private static final ThreadLocal<String> emailHolder = new ThreadLocal<>();

    public static void setEmail(String email) {
        emailHolder.set(email);
    }

    public static String getEmail() {
        return emailHolder.get();
    }

    public static void clear() {
        emailHolder.remove();
    }
}
