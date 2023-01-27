package com.learncha.api.auth.domain;

import com.learncha.api.common.abstractentity.TimeStamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "member_refresh_token")
public class MemberRefreshToken extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String memberToken;

    @Column(nullable = false)
    private String refreshToken;

    public MemberRefreshToken(String memberToken, String refreshToken) {
        this.memberToken = memberToken;
        this.refreshToken = refreshToken;
    }

    public static MemberRefreshToken of(String memberToken, String refreshToken) {
        return new MemberRefreshToken(memberToken, refreshToken);
    }
}
