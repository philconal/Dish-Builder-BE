package com.conal.dishbuilder.util;

import com.conal.dishbuilder.constant.CommonStatus;

public class CommonUtils {

    public static String from(int value) {
        return CommonStatus.values()[value].toString();
    }

    public static String from(CommonStatus value) {
        return value.name();
    }

    public static int getValue(CommonStatus value) {
        return value.ordinal();
    }
}
