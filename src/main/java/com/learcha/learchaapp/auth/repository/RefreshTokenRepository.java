package com.learcha.learchaapp.auth.repository;

import com.learcha.learchaapp.auth.domain.MemberRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {

}
