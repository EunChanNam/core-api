package com.learcha.learchaapp.auth.controller;

import com.learcha.learchaapp.auth.controller.MemberDto.SignUpRequest;
import com.learcha.learchaapp.auth.controller.MemberDto.SignUpResponse;
import com.learcha.learchaapp.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthApiController {
    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest memberSignUpRequest) {
        log.info("Sign Up Request: {}", memberSignUpRequest.toString());
        return ResponseEntity.ok(authService.signUpMember(memberSignUpRequest));
    }

}
