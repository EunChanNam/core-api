package com.learcha.learchaapp.member.controller;

import com.learcha.learchaapp.member.domain.Member;
import lombok.Getter;

public class MemberDto {

    @Getter
    public static class MemberSignUpRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String authType;

        public Member toEntity() {
            return Member.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .authType(authType)
                .build();
        }
    }
}
