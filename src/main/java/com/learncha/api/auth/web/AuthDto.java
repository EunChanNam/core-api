package com.learncha.api.auth.web;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
        @NotBlank(message = "탈퇴사유는 필수 값 입니다.")
        private String etcMsg;
    }

    @Getter
    public static class SignUpResponse {
        private final String email;
        private final String memberToken;
        private final String authType;

        @Builder
        public SignUpResponse(String authType, String email, String memberToken) {
            this.authType = authType;
            this.email = email;
            this.memberToken = memberToken;
        }
    }

    @Getter
    public static class EmailDuplicationResult {
        private final String email;
        private final String isDuplicated;

        @Builder
        public EmailDuplicationResult(String email, boolean isDuplicated) {
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
    public static class InvalidPasswordResponse {
        private final String message;
        public InvalidPasswordResponse() {
            this.message = "Invalid Password";
        }
    }
}
