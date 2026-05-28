package com.paycoms.cp7.global.error;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String errorCode;
    private final Object[] args;

    public BusinessException(String errorCode, Object... args) {
        super(errorCode);
        this.errorCode = errorCode;
        this.args = args;
    }
}
