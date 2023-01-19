package com.learcha.learchaapp.member.service;

import com.learcha.learchaapp.member.controller.MemberDto.MemberSignUpRequest;
import com.learcha.learchaapp.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private void signUpMember(MemberSignUpRequest signUpRequest) {
        // todo
        // 2. 패스워드 암호화
        signUpRequest.getPassword();
        signUpRequest.toEntity();
        // 1. member 생성
        // 3. DB 저장
    }

    private void emailAuthentication() {

    }

}
