package com.learncha.api.auth.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learncha.api.auth.domain.Member.AuthType;
import com.learncha.api.common.exception.InvalidParamException;
import com.learncha.api.common.security.jwt.model.JWTManager.JwtTokenBox;
import java.util.List;
import java.util.function.Function;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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
        @NotBlank(message = "'이름'은 필수 값입니다.")
        private String firstName;
        @NotBlank(message = "'성'은 필수 값입니다.")
        private String lastName;
        @Pattern(regexp = "[a-zA-Z0-9]{1,64}@[a-zA-Z0-9_\\-\\.]{1,255}", message = "유효하지 않은 이메일 입니다. 양식을 확인해주세요.")
        @NotBlank(message = "email은 필수 값입니다.")
        private String email;

        @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 최대 15자로 설정되어야 합니다.")
        @NotBlank(message = "pasword는 필수 값입니다.")
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
    public static class LoginInfo {
        private final String email;
        private final String accessToken;
        private final String refreshToken;
        private final String fullName;
        private final String authType;

        private LoginInfo(String email, String accessToken, String refreshToken, String authType, String fullName) {
            this.email = email;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.authType = authType;
            this.fullName = fullName;

        }

        public static LoginInfo of(JwtTokenBox tokenBox, String email, String fullName) {
            return new LoginInfo(
                email,
                tokenBox.getAccessToken(),
                tokenBox.getRefreshToken(),
                tokenBox.getAuthType(),
                fullName
            );
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
    public static class EmailAvailableCheckResponse {
        private final String email;
        private final String isDuplicated;

        @Builder
        private EmailAvailableCheckResponse(String email, boolean isDuplicated) {
            this.email = email;
            this.isDuplicated = isDuplicated ? "TRUE" : "FALSE";
        }

        public static EmailAvailableCheckResponse availableEmail(String email) {
            return EmailAvailableCheckResponse.builder()
                .email(email)
                .isDuplicated(false)
                .build();
        }

        public static EmailAvailableCheckResponse unavailable(String email) {
            return EmailAvailableCheckResponse.builder()
                .email(email)
                .isDuplicated(true)
                .build();
        }
    }

    @Getter
    public static class LoginSuccessResponse {
        private final String email;
        private final String name;
        private final String authType;
        private final String accessToken;

        @Builder
        private LoginSuccessResponse(String email, String name, String authType, String accessToken) {
            this.email = email;
            this.name = name;
            this.authType = authType;
            this.accessToken = accessToken;
        }

        public static LoginSuccessResponse of(LoginInfo loginInfo) {
            return LoginSuccessResponse.builder()
                .email(loginInfo.getEmail())
                .name(loginInfo.getFullName())
                .accessToken(loginInfo.getAccessToken())
                .authType(loginInfo.getAuthType())
                .build();
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
