package com.learncha.api.auth.domain;

import com.learncha.api.auth.web.AuthDto.SignUpRequest;
import com.learncha.api.common.abstractentity.TimeStamp;
import com.learncha.api.common.exception.InvalidParamException;
import com.learncha.api.common.util.TokeGenerator;
import java.util.Objects;
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


    @Getter
    @RequiredArgsConstructor
    public enum Status {
        NEED_CERTIFICATED("NEED CERTIFICATED"),
        CERTIFICATED("CERTIFICATED"),
        ACTIVE("ACTIVE"),
        DELETED("DELETED");
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
        ROLE_USER("ROLE_USER"),
        ROLE_ADMIN("ROLE_ADMIN");
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
        this.status = Status.NEED_CERTIFICATED;
        this.authType = authType;
    }

    public Member(String email, AuthType google, String firstName, String lastName) {
        this.email = email;
        this.memberToken = TokeGenerator.randomCharacterWithPrefix(MEMBER_PREFIX);
        this.authType = google;
        this.firstName = firstName;
        this.lastName =  lastName;
        this.status = Status.ACTIVE;
        this.authority = MemberRole.ROLE_USER;
    }

    /**
     * 이메일 인증 코드 체크를 위해 EMAIL_TYPE 의 Member 를 생성
     */
    public static Member createInitEmailAuthTypeMemberForAuthCode(String email, AuthType authType) {
        return new Member(email, authType);
    }

    public static Member createGoogleAuthMember(GoogleUserProfile googleUserProfile) {
        return new Member(
            googleUserProfile.getEmail(),
            AuthType.GOOGLE,
            googleUserProfile.getGivenName(),
            googleUserProfile.getFamilyName()
        );
    }

    public Member updateToEmailActiveUser(SignUpRequest signUpRequest, String encodedPw) {
        this.password = encodedPw;
        this.firstName = signUpRequest.getFirstName();
        this.lastName = signUpRequest.getLastName();
        this.authority = MemberRole.ROLE_USER;
        this.status = Status.ACTIVE;
        return this;
    }

    public String getFullName() {
        return lastName + firstName;
    }


    public boolean isNeedEmailAuthentication() {
        return Objects.equals(this.status, Status.NEED_CERTIFICATED);
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

    public Member resetToInitMember() {
        this.password = null;
        this.reasonWithdrawal = null;
        this.status = Status.NEED_CERTIFICATED;
        this.authType = AuthType.EMAIL;
        return this;
    }

    public Member updateMemberInfoFromGoogle(GoogleUserProfile googleUserProfile) {
        this.lastName = googleUserProfile.getFamilyName();
        this.firstName = googleUserProfile.getGivenName();
        return this;
    }

    public void checkDuplicateSignUp() {
        if(this.status == Status.ACTIVE) {
            throw new InvalidParamException("이미 가입된 사용자입니다.");
        }
    }

    public void checkNeedCertificated() {
        if(this.status == Status.NEED_CERTIFICATED) {
            throw new InvalidParamException("이메일 인증이 필요합니다.");
        }
    }

    public void onDelete() {
        this.status = Status.DELETED;
    }

    public void setDeleteReason(String reason) {
        this.reasonWithdrawal = reason;
    }

    public void emailAuthenticationSuccess() {
        this.status = Status.CERTIFICATED;
    }

    public boolean isCertificated() { return Objects.equals(this.status, Status.CERTIFICATED);}

    public boolean isDeleted() { return Objects.equals(this.status, Status.DELETED);}

    public void updatePwToTemporaryPW(String tempPassword) {
        this.password = tempPassword;
    }

    public void updateNewPassword(String newPassword) {
        this.password = newPassword;
    }

    public boolean isActive() {
        return Objects.equals(this.status, Status.ACTIVE);
    }
}
