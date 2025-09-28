package com.conal.dishbuilder.constant;

public class Constants {

    public final static class APP_VERSION {
        private final static String V1_0 = "v1.0";
    }

    public final static String APP_CONTEXT_PATH_V1_0 = "/" + APP_VERSION.V1_0;

    public final static class Endpoint {
        public final static String AUTH = APP_CONTEXT_PATH_V1_0 + "/auth";
        public static final String TENANT = APP_CONTEXT_PATH_V1_0 + "/tenant";
        public static final String USER = APP_CONTEXT_PATH_V1_0 + "/user";
        public static final String CATEGORY = APP_CONTEXT_PATH_V1_0 + "/category";
        public static final String INGREDIENTS = APP_CONTEXT_PATH_V1_0 + "/ingredients";
        public static final String DISH = APP_CONTEXT_PATH_V1_0 + "/dish";
    }

    public static class Tenant {
        public final static String NOT_FOUND = "TENANT NOT FOUND";
    }

    public static class User {
        public final static String NOT_FOUND = "USER NOT FOUND";
        public final static String TOO_MANY_REQUESTS = "TOO_MANY_REQUESTS";
        public final static String TOO_FAST = "TOO_FAST";
    }

    public static final int MAX_RETRIES = 5;
    public static final int START = 0;
    public static final int END = -1;
    public static int EXPIRY_TIME = 5; //mins
    public static int OTP_LENGTH = 6; //mins
    public static String FORGOT_PASSWORD_OTP_TEMPLATE_FILE_PATH = "templates/otp-mail.html";
    public static String SUBJECT = "[Dish-Builder] Xác thực tài khoản - OTP của bạn";
}
