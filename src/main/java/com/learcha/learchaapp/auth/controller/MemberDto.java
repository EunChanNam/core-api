package com.learcha.learchaapp.auth.controller;

import com.learcha.learchaapp.auth.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class MemberDto {

    @ToString
    @Getter
    public static class SignUpRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String authType;

        public Member toEntity(String encodedPW) {
            return Member.builder()
                .email(email)
                .password(encodedPW)
                .firstName(firstName)
                .lastName(lastName)
                .authType(authType)
                .build();
        }
    }


    @Getter
    public static class SignUpResponse {
        private final String authType;
        private final String email;
        private final String memberToken;

        @Builder
        public SignUpResponse(String authType, String email, String memberToken) {
            this.authType = authType;
            this.email = email;
            this.memberToken = memberToken;
        }
    }
}
