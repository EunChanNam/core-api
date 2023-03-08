package com.learncha.api.auth.web;

import com.learncha.api.auth.service.AuthService;
import com.learncha.api.auth.web.AuthDto.AccessTokenResponse;
import com.learncha.api.auth.web.AuthDto.AuthCodeResult;
import com.learncha.api.auth.web.AuthDto.EmailAvailableCheckResponse;
import com.learncha.api.auth.web.AuthDto.LoginInfo;
import com.learncha.api.auth.web.AuthDto.LoginSuccessResponse;
import com.learncha.api.auth.web.AuthDto.MemberVerifyResponse;
import com.learncha.api.auth.web.AuthDto.PasswordUpdateDto;
import com.learncha.api.auth.web.AuthDto.SignUpRequest;
import com.learncha.api.auth.web.AuthDto.SignUpResponse;
import com.learncha.api.auth.web.AuthDto.VerifyRequestDto;
import com.learncha.api.common.exception.InvalidParamException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> login(@RequestBody @Valid AuthDto.LoginRequestDto loginDto) {
        LoginInfo loginInfo = authService.login(loginDto);
        LoginSuccessResponse res = LoginSuccessResponse.of(loginInfo);
        String refreshCookie = createCookieOfRefreshToken(loginInfo.getRefreshToken());
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Set-Cookie", refreshCookie);
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginSuccessResponse> logout(
        HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();
        try {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("refresh_token")) {
                    cookie.setMaxAge(0);
                }
            }
        } catch(NullPointerException ex) {
            throw new InvalidParamException("이미 로그아웃된 계정입니다.");
        }

        String cookie = cookieExpire();
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Set-Cookie", cookie);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody @Validated SignUpRequest memberSignUpRequest) {
        log.info("Sign Up Request: {}", memberSignUpRequest.toString());
        SignUpResponse res = authService.signUpMember(memberSignUpRequest);

        String refreshCookie = createCookieOfRefreshToken(res.getRefreshToken());
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Set-Cookie", refreshCookie);

        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<EmailAvailableCheckResponse> isEmailAvailable(
        @RequestParam @NotBlank(message = "이메일은 필수 값입니다.") String email
    ) {
        log.info("request email: {}", email);
        return ResponseEntity.ok(authService.isAvailableEmail(email));
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

    @GetMapping("/access-token")
    public ResponseEntity<AccessTokenResponse> getAccessTokenUsingRefreshToken(
        @CookieValue(name = "refresh_token") String refreshToken
    ) {
        return ResponseEntity.ok(authService.getAccessToken(refreshToken));
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteMember(
        @RequestBody @Valid AuthDto.DeleteMemberRequestDto deleteMemberDto
    ) {
        authService.deleteMember(deleteMemberDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<MemberVerifyResponse> memberVerify(@RequestBody VerifyRequestDto verifyRequestDto) {
        boolean res = authService.verifyMember(verifyRequestDto);

        return ResponseEntity.ok(new MemberVerifyResponse(res));
    }

    /**
     * 임시 패스워드 발급
     */
    @GetMapping("/temporary-password")
    public ResponseEntity<Void> getTemporaryPassword(
        @RequestParam @NotBlank(message = "Email is required") String email) {
        authService.sendTemporaryPasswordAndUpdatePasswordToTemporary(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("")
    public ResponseEntity<Void> modifyPassword(@RequestBody PasswordUpdateDto updatePasswordDto) {
        authService.updatePasswordToNewPassword(updatePasswordDto);
        return ResponseEntity.ok().build();
    }

    private String createCookieOfRefreshToken(String refreshToken) {
        ResponseCookieBuilder refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken);
        refreshTokenCookie.httpOnly(true);
        refreshTokenCookie.path("/");
        refreshTokenCookie.sameSite("None");
        return refreshTokenCookie.build().toString();
    }

    private String cookieExpire() {
        ResponseCookieBuilder cookie = ResponseCookie.from("refresh_token", null);
        cookie.httpOnly(true);
        cookie.maxAge(0);
        cookie.path("/");
        cookie.sameSite("None");
        return cookie.build().toString();
    }
}
