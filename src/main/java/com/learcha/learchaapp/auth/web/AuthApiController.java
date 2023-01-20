package com.learcha.learchaapp.auth.web;

import com.learcha.learchaapp.auth.web.AuthDto.EmailDuplicationResult;
import com.learcha.learchaapp.auth.web.AuthDto.SignUpRequest;
import com.learcha.learchaapp.auth.web.AuthDto.SignUpResponse;
import com.learcha.learchaapp.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("")
    public ResponseEntity<EmailDuplicationResult> checkDuplicatedEmail(@RequestParam String email) {
        log.info("request email: {}", email);
        boolean isDuplicated = authService.isAvailableEmail(email);

        EmailDuplicationResult response = EmailDuplicationResult.builder()
            .email(email)
            .isDuplicated(isDuplicated)
            .build();

        return ResponseEntity.ok(response);
    }
}
