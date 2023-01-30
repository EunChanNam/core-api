package com.learncha.api.common.security.jwt.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenVerifyResult {

    private final boolean result;
    private final String email;
    private final String message;

    @Builder
    public TokenVerifyResult(boolean result, String username, String message) {
        this.result = result;
        this.email = username;
        this.message = message;
    }

    public boolean isVerified() {
        return this.result;
    }

}
