package com.learcha.learchaapp.auth.domain;

import com.learcha.learchaapp.auth.web.AuthDto.SignUpRequest;
import com.learcha.learchaapp.common.abstractentity.TimeStamp;
import com.learcha.learchaapp.common.exception.InvalidParamException;
import com.learcha.learchaapp.common.util.TokeGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member extends TimeStamp {

    private final static String MEMBER_PREFIX = "mem_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String memberToken;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private AuthType authType;

    @Column(nullable = true)
    private String reasonWithdrawal;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private MemberRole authority;

    @Column(nullable = true)
    private String authenticationCode;

    public Member updateEmailAuthenticatedUser(SignUpRequest signUpRequest, String encodedPw) {
        this.password = encodedPw;
        this.firstName = signUpRequest.getFirstName();
        this.lastName = signUpRequest.getLastName();
        this.authority = MemberRole.ROLE_USER;
        return this;
    }


    @Getter
    @RequiredArgsConstructor
    public enum Status {
        NEED_AUTHENTICATED("인증 필요"),
        AUTHENTICATED("인증_완료");
        private final String description;

    }

    @Getter
    @RequiredArgsConstructor
    public enum AuthType {
        EMAIL("EMAIL"),
        GOOGLE("GOOGLE");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum MemberRole {
        ROLE_USER("USER"),
        ROLE_ADMIN("ADMIN");
        private final String description;
    }

    @Builder
    public Member(
        String email,
        AuthType authType
    ) {
        if(StringUtils.isEmpty(email)) throw new InvalidParamException("Email is required!!");
        if(authType == null) throw new InvalidParamException("auth type never be null");

        this.memberToken = TokeGenerator.randomCharacterWithPrefix(MEMBER_PREFIX);
        this.email = email;
        this.status = Status.NEED_AUTHENTICATED;
        this.authType = authType;
    }

    public static Member createInitEmailMember(String email, AuthType authType) {
        return new Member(email, authType);
    }

    public void setRoleAdmin() {
        this.authority = MemberRole.ROLE_ADMIN;
    }

    public void setRoleUser() {
        this.authority = MemberRole.ROLE_USER;
    }

    public void registerReasonOfWithdrawal(String reason) {
        if(reason == null) throw new InvalidParamException("reason of withdrawal never be null");
        this.reasonWithdrawal = reason;
    }

    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public void emailAuthenticationSuccess() {
        this.status = Status.AUTHENTICATED;
    }
}
