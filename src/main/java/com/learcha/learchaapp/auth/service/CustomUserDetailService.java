package com.learcha.learchaapp.auth.service;

import com.learcha.learchaapp.auth.domain.Member;
import com.learcha.learchaapp.auth.domain.MemberRefreshToken;
import com.learcha.learchaapp.auth.repository.MemberRepository;
import com.learcha.learchaapp.auth.repository.RefreshTokenRepository;
import com.learcha.learchaapp.common.exception.EntityNotFoundException;
import com.learcha.learchaapp.common.util.jwt.model.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("email: {}", email);
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(EntityNotFoundException::new);
        return new UserDetailsImpl(member);
    }

    public void registerRefreshToken(Member member, String refreshToken) {
        MemberRefreshToken refreshTokenEntity = MemberRefreshToken.of(member.getMemberToken(), refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
    }
}
