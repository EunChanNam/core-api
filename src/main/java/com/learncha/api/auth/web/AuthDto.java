package com.learncha.api.auth.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learncha.api.auth.domain.Member.AuthType;
import com.learncha.api.common.exception.InvalidParamException;
import java.util.List;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

public class AuthDto {

    @ToString
    @Getter
    public static class SignUpRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
    }

    @ToString
    @Getter
    public static class LoginRequestDto {
        private String email;
        private String password;
    }

    @NoArgsConstructor
    @Getter
    public static class DeleteMemberRequestDto {
        private String email;
        @Size(min=1, message = "최소 1개의 사유를 선택해주세요.")
        private List<String> selectedReason;
        private String etcMsg;
    }

    @Getter
    public static class VerifyRequestDto {
        private final String authType;
        private final String email;
        private final String password;

        public VerifyRequestDto(String authType, String email, String password) {
            if( ! isAuthTypeEmail(authType) &&
                ! isAuthTypeGoogle(authType)
            )
                throw new InvalidParamException("잘못된 인증방식이 전달되었습니다.");

            if(isAuthTypeEmail(authType) && StringUtils.isBlank(password))
                throw new InvalidParamException("Password는 필수 값 입니다.");

            if(StringUtils.isBlank(email))
                throw new InvalidParamException("Email은 필수 값입니다.");

            this.authType = authType;
            this.email = email;
            this.password = password;
        }

        private boolean isAuthTypeEmail(String authType) {
            return authType.equals(AuthType.EMAIL.getDescription());
        }

        private boolean isAuthTypeGoogle(String authType) {
            return authType.equals(AuthType.GOOGLE.getDescription());
        }
    }

    @Getter
    public static class SignUpResponse {
        private final String email;
        private final String memberToken;
        private final String accessToken;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final String refreshToken;
        private final String authType;

        @Builder
        public SignUpResponse(
            String email,
            String memberToken,
            String accessToken,
            String refreshToken,
            String authType
        ) {
            this.email = email;
            this.memberToken = memberToken;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.authType = authType;
        }
    }

    @Getter
    public static class EmailAvliableCheckResponse {
        private final String email;
        private final String isDuplicated;

        @Builder
        public EmailAvliableCheckResponse(String email, boolean isDuplicated) {
            this.email = email;
            this.isDuplicated = isDuplicated ? "TRUE" : "FALSE";
        }
    }

    @Getter
    public static class LoginSuccessResponse {
        private final String email;
        private final String authType;
        private final String accessToken;

        @Builder
        public LoginSuccessResponse(String email, String authType, String accessToken) {
            this.email = email;
            this.authType = authType;
            this.accessToken = accessToken;
        }
    }

    @Getter
    public static class AuthCodeResult {
        private final String email;
        private final String result;

        public AuthCodeResult(String email, boolean result) {
            this.email = email;
            this.result = result ? "SUCCESS" : "FAIL";
        }
    }

    @Getter
    public static class PasswordUpdateDto {
        private final String email;
        private final String password;
        private final String newPassword;
        private final String newPasswordConfirm;

        public PasswordUpdateDto(String email, String password, String newPassword, String newPasswordConfirm) {
            if(! StringUtils.equals(newPassword, newPasswordConfirm)) {
                throw new InvalidParamException("변경하고자 하는 패스워드가 일치하지 않습니다.");
            }

            this.email = email;
            this.password = password;
            this.newPassword = newPassword;
            this.newPasswordConfirm = newPasswordConfirm;
        }
    }

    @Getter
    public static class MemberVerifyResponse {
        private final String result;

        public MemberVerifyResponse(boolean result) {
            this.result = result ? "TRUE" : "FALSE";
        }
    }

    @Getter
    public static class AccessTokenResponse {
        private final String accessToken;

        public AccessTokenResponse(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
