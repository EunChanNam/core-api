package com.learcha.learchaapp.member.domain;

import com.learcha.learchaapp.member.common.exception.InvalidParamException;
import com.learcha.learchaapp.member.common.util.TokeGenerator;
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


@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    private final String MEMBER_PREFIX = "mem";

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


    @Getter
    @RequiredArgsConstructor
    private enum Status {
        NEED_AUTHENTICATED("인증 필요"),
        AUTHENTICATED("인증_완료"),
        EXPIRED("이메일 인증 만료");
        private final String description;

    }

    @Getter
    private enum AuthType {
        EMAIL,
        GOOGLE;
    }

    @Getter
    private enum MemberRole {
        USER
    }

    @Builder
    public Member (
        String email,
        String password,
        String firstName,
        String lastName,
        String authType
    ) {
        if(StringUtils.isEmpty(email)) throw new InvalidParamException("Email is required!!");
        if(StringUtils.isEmpty(password)) throw new InvalidParamException("Password is required!!");
        if(StringUtils.isEmpty(firstName)) throw new InvalidParamException("first name is required!!");
        if(StringUtils.isEmpty(lastName)) throw new InvalidParamException("last name is required!!");
        if(authType == null) throw new InvalidParamException("auth type never be null");

        this.memberToken = TokeGenerator.randomCharacterWithPrefix(MEMBER_PREFIX);
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = Status.NEED_AUTHENTICATED;
        this.authType = authType.equals("EMAIL") ? AuthType.EMAIL : AuthType.GOOGLE;
        this.authority = MemberRole.USER;
    }

    public void changeAuthority(MemberRole authority) {
        if(authority == null) throw new RuntimeException("authority is never be null");
        this.authority = authority;
    }

    public void registerReasonOfWithdrawal(String reason) {
        if(reason == null) throw new InvalidParamException("reason of withdrawal never be null");
        this.reasonWithdrawal = reason;
    }
}
