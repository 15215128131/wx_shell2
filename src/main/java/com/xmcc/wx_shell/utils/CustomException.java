package com.xmcc.wx_shell.utils;

import com.xmcc.wx_shell.common.ResultEnums;

public class CustomException extends RuntimeException {

    private int code;

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        this(ResultEnums.FAIL.getCode(),message);
    }

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
    }
}
