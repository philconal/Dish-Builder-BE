package com.conal.dishbuilder.constant;

import lombok.Getter;

@Getter
public enum CommonStatus {
    DEFAULT(1),
    ACTIVE(2),
    INACTIVE(3);
    private final int status;

    CommonStatus(int status) {
        this.status = status;
    }
}
