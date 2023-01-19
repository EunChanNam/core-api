package com.learcha.learchaapp.auth.service;

import com.learcha.learchaapp.auth.repository.MemberRepository;
import com.learcha.learchaapp.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        memberRepository.findByEmail(email)
            .orElseThrow(EntityNotFoundException::new);
        return null;
    }
}
