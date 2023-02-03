package com.learncha.api.common.exception;

import com.learncha.api.common.error.ErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseException extends RuntimeException {

    private ErrorCode errorCode;

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getErrorMsg());
        this.errorCode = errorCode;
    }

    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
