package com.learncha.api.auth.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table
public class AuthCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;
    @Column(nullable = false, unique = true)
    private String authCode; // 인증 코드
    @Column(nullable = false)
    private LocalDateTime expireTime;

    @Builder
    public AuthCode(Member member, String authCode) {
        this.member = member;
        this.authCode = authCode;
        this.expireTime = LocalDateTime.now().plusDays(1L);
    }

    public void changeAuthCode(String authCode) {
        this.authCode = authCode;
        this.expireTime = LocalDateTime.now().plusDays(1L);
    }
}
