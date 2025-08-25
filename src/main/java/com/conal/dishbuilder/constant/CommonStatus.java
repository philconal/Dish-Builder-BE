package com.conal.dishbuilder.constant;

import lombok.Getter;

@Getter
public enum CommonStatus {
    ACTIVE(1),
    INACTIVE(2);
    private final int status;

    CommonStatus(int status) {
        this.status = status;
    }
}
