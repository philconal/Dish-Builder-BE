package com.conal.dishbuilder.constant;

import lombok.Getter;

@Getter
public enum UserType {
    CUSTOMER(1),

    SALE(2),
    ADMIN(3),

    SUPER_ADMIN(4);

    private final int value;

    UserType(final int value) {
        this.value = value;
    }
}
