package com.learncha.api.auth.web;

import com.learncha.api.auth.service.AuthService;
import com.learncha.api.auth.web.AuthDto.AuthCodeResult;
import com.learncha.api.auth.web.AuthDto.EmailDuplicationResult;
import com.learncha.api.auth.web.AuthDto.LoginSuccessResponse;
import com.learncha.api.auth.web.AuthDto.SignUpRequest;
import com.learncha.api.auth.web.AuthDto.SignUpResponse;
import com.learncha.api.common.security.jwt.model.JWTManager.JwtTokenBox;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
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

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendAuthCode(
        @RequestParam @NotBlank(message = "email never be empty") String email
    ) {
        log.info("email: {}", email);
        authService.emailAuthentication(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/confirm-code")
    public ResponseEntity<AuthCodeResult> getEmailAuthResult(
        @RequestParam @NotBlank(message = "auth code never be empty") String authCode,
        @RequestParam @NotBlank(message = "email never be empty") String email
    ) {
        boolean res = authService.getAuthResult(authCode, email);
        return ResponseEntity.ok(new AuthCodeResult(email, res));
    }

    @DeleteMapping("/email")
    public ResponseEntity<Void> removeAuth(@RequestParam String email) {
        authService.removeAuth(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> login(@RequestBody @Valid AuthDto.LoginDto loginDto) {
        JwtTokenBox jwtTokenBox = authService.login(loginDto);

        LoginSuccessResponse res = LoginSuccessResponse.builder()
            .email(loginDto.getEmail())
            .accessToken(jwtTokenBox.getAccessToken())
            .authType(jwtTokenBox.getAuthType())
            .build();

        String refreshCookie = createCookieOfRefreshToken(jwtTokenBox.getRefreshToken());
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Set-Cookie", refreshCookie.toString());

        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    private String createCookieOfRefreshToken(String refreshToken) {
        ResponseCookieBuilder refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken);
        refreshTokenCookie.httpOnly(true);
        refreshTokenCookie.path("/");
        refreshTokenCookie.sameSite("None");
        return refreshTokenCookie.build().toString();
    }
}
