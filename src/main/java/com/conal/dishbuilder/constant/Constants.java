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
    }

    public static class Tenant {
        public final static String NOT_FOUND = "TENANT NOT FOUND";
    }
}
