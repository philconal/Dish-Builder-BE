package com.conal.dishbuilder.context;

import java.util.UUID;

public class UserContextHolder {
    private static final ThreadLocal<String> userContext = new ThreadLocal<>();

    public static void setUserContext(String username) {
        userContext.set(username);
    }

    public static String getUserContext() {
        return userContext.get();
    }

    public static void clearUserContext() {
        userContext.remove();
    }
}
