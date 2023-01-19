package com.learcha.learchaapp.auth.service;

import com.learcha.learchaapp.auth.controller.MemberDto.SignUpRequest;
import com.learcha.learchaapp.auth.controller.MemberDto.SignUpResponse;
import com.learcha.learchaapp.auth.domain.Member;
import com.learcha.learchaapp.auth.repository.MemberRepository;
import com.learcha.learchaapp.common.exception.InvalidParamException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public SignUpResponse signUpMember(SignUpRequest signUpRequest) {
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        String email = signUpRequest.getEmail();

        if(memberRepository.existsMemberByEmail(email))
            throw new InvalidParamException("Already Exist Email");

        Member member = signUpRequest.toEntity(encodedPassword);
        memberRepository.save(member);

        return SignUpResponse.builder()
            .email(email)
            .memberToken(member.getMemberToken())
            .authType(member.getAuthType().getDescription())
            .build();
    }

    private void emailAuthentication(String email) {

    }

}
