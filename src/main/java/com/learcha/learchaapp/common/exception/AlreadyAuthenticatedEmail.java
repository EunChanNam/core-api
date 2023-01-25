package com.learcha.learchaapp.common.exception;

import com.learcha.learchaapp.common.error.ErrorCode;

public class AlreadyAuthenticatedEmail extends BaseException {

    public AlreadyAuthenticatedEmail() {
        super(ErrorCode.ALREADY_AUTHENTICATED_EMAIL);
    }

    public AlreadyAuthenticatedEmail(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
