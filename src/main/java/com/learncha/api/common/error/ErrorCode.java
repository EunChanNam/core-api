package com.learncha.api.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    COMMON_SYSTEM_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), // 장애 상황
    COMMON_INVALID_PARAMETER("요청한 값이 올바르지 않습니다."),
    COMMON_ENTITY_NOT_FOUND("존재하지 않는 엔티티입니다."),
    MISSING_REFRESH_TOKEN("access_token 재발급을 위해선 refresh token이 필요합니다."),
    ALREADY_AUTHENTICATED_EMAIL("이미 인증된 이메일입니다."),
    EMAIL_NOT_EXIST("등록되지 않은 Email 입니다.")
    ;

    private final String errorMsg;
    public String getErrorMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}
