package com.learncha.api.auth.repository;

import com.learncha.api.auth.domain.MemberRefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {

    Optional<MemberRefreshToken> findByMemberToken(String memberToken);

}
