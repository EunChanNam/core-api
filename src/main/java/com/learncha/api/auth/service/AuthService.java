package com.learncha.api.auth.service;

import com.learncha.api.auth.domain.AuthCode;
import com.learncha.api.auth.domain.Member;
import com.learncha.api.auth.domain.Member.Status;
import com.learncha.api.auth.domain.MemberRefreshToken;
import com.learncha.api.auth.repository.MemberRepository;
import com.learncha.api.auth.repository.RefreshTokenRepository;
import com.learncha.api.auth.web.AuthDto.AccessTokenResponse;
import com.learncha.api.auth.web.AuthDto.DeleteMemberRequestDto;
import com.learncha.api.auth.web.AuthDto.EmailAvailableCheckResponse;
import com.learncha.api.auth.web.AuthDto.LoginInfo;
import com.learncha.api.auth.web.AuthDto.LoginRequestDto;
import com.learncha.api.auth.web.AuthDto.PasswordUpdateDto;
import com.learncha.api.auth.web.AuthDto.SignUpRequest;
import com.learncha.api.auth.web.AuthDto.SignUpResponse;
import com.learncha.api.auth.web.AuthDto.VerifyRequestDto;
import com.learncha.api.common.error.ErrorCode;
import com.learncha.api.common.exception.EntityNotFoundException;
import com.learncha.api.common.exception.InvalidJwtTokenException;
import com.learncha.api.common.exception.InvalidParamException;
import com.learncha.api.common.exception.RefreshTokenExpiredException;
import com.learncha.api.common.security.jwt.model.JWTManager;
import com.learncha.api.common.security.jwt.model.JWTManager.JwtTokenBox;
import com.learncha.api.common.security.jwt.model.JWTManager.TokenVerifyResult;
import com.learncha.api.common.security.jwt.model.UserDetailsImpl;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService customUserDetailService;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTManager jwtManager;
    private final SpringTemplateEngine templateEngine;

    @Value("spring.mail.username")
    private String mailServerUsername;

    @Transactional
    public LoginInfo login(LoginRequestDto loginDto) {
        String loginEmail = loginDto.getEmail();
        UserDetailsImpl userDetails = customUserDetailService.loadUserByUsername(loginEmail);
        userDetails.checkValidation();
        passwordMatchingCheck(loginDto.getPassword(), userDetails.getPassword());
        JwtTokenBox tokenBox = jwtManager.generateTokenBox(userDetails);;

        refreshTokenRepository.save(
            MemberRefreshToken.of(userDetails.getMember().getMemberToken(), tokenBox.getRefreshToken())
        );

        return LoginInfo.of(
            tokenBox,
            userDetails.getUsername(),
            userDetails.getMember().getFullName()
        );
    }

    public AccessTokenResponse getAccessToken(String refreshToken) {
        TokenVerifyResult verifyResult;
        try {
            verifyResult = jwtManager.verifyToken(refreshToken);
        } catch(ExpiredJwtException ex) {
            throw new RefreshTokenExpiredException("재 로그인이 필요합니다.");
        }

        if(! verifyResult.isVerified())
            throw new InvalidJwtTokenException(verifyResult.getMessage());

        String email = verifyResult.getEmail();
        String accessToken = jwtManager.generateAccessToken(email);
        UserDetailsImpl member = customUserDetailService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            member.getUsername(),
            member.getPassword(),
            member.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new AccessTokenResponse(accessToken);

    }

    @Transactional
    public SignUpResponse signUpMember(SignUpRequest signUpRequest) {
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        String email = signUpRequest.getEmail();

        Member storedMember = memberRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidParamException("해당 Email로 인증된 사용자가 없습니다."));

        storedMember.checkDuplicateSignUp();
        storedMember.checkNeedCertificated();

        storedMember.updateToEmailActiveUser(signUpRequest, encodedPassword);
        Member activeUser = memberRepository.save(storedMember);
        JwtTokenBox tokenBox = jwtManager.generateTokenBox(activeUser.getEmail());

        return SignUpResponse.builder()
            .email(email)
            .memberToken(activeUser.getMemberToken())
            .accessToken(tokenBox.getAccessToken())
            .authType(activeUser.getAuthType().getDescription()).build();
    }

    // todo 제거 가능한지 알아보기
    @Transactional
    public void emailAuthentication(String email) {
        String authCode = createRandomStrings();
        memberRepository.findByEmail(email)
            .ifPresentOrElse(
                member -> {
                    if(member.isActive()) throw new InvalidParamException("이미 가입된 이메일 입니다.");
                    else if(member.isDeleted()) {
                        // todo 인증코드 발급 객체 생성하여 생성자에서 생성하도록 변경
                        Member initMember = Member.createInitEmailTypeMemberForAuthCode(email);
                        AuthCode authCodeEntity = AuthCode.builder().member(initMember).authCode(authCode).build();
                        initMember.addAuthCoe(authCodeEntity);
                        sendAuthCodeEmail(email, authCode);
                        memberRepository.save(initMember);
                    } else {
                        // NEED CERTIFICATED or CERTIFICATED
                        // 인증 정보를 덮어씌우고 인증 메일 발송
                        // 이미 생성된 member token의 주인은 새롭게 시도하는 사람
                        member.changeToNewAuthCode(authCode);
                        memberRepository.save(member);
                        sendAuthCodeEmail(email, authCode);
                    }
                },
                // 신규 유저
                () -> {
                    Member member = Member.createInitEmailTypeMemberForAuthCode(email);
                    AuthCode authCodeEntity = AuthCode.builder().member(member).authCode(authCode).build();
                    member.addAuthCoe(authCodeEntity);
                    memberRepository.save(member);
                    sendAuthCodeEmail(email, authCode);
                });
    }

    public boolean getAuthResult(String authCode, String email) {
        // 인증 후 재 요청하면 NEED CERTIFICATED로 변경?
        Member member = memberRepository.findByEmailAndStatusIs(email, Status.NEED_CERTIFICATED)
            .orElseThrow(InvalidParamException::new);

        var authCodeEntity = member.getAuthCode();
        String savedAuthCode = authCodeEntity.getAuthCode();

        var now = LocalDateTime.now();

        if(now.compareTo(authCodeEntity.getExpireTime()) > 0)
            throw new InvalidParamException("만료된 인증 코드입니다.");

        if(savedAuthCode.equals(authCode))
            member.emailAuthenticationSuccess();

        memberRepository.save(member);
        return savedAuthCode.equals(authCode);
    }

    @Transactional(readOnly = true)
    public EmailAvailableCheckResponse isAvailableEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmailAndStatusIsActive(email);

        return optionalMember.map(Member::getEmail)
            .map(EmailAvailableCheckResponse::unavailable)
            .orElse(EmailAvailableCheckResponse.availableEmail(email));
    }

    @Transactional
    public void deleteMember(DeleteMemberRequestDto deleteMemberDto) {
        String email = deleteMemberDto.getEmail();

        Member member = memberRepository.findByEmailAndStatusIsNotDeleted(email)
            .orElseThrow(() -> new InvalidParamException("Already Deleted Member"));

        StringBuffer deletedReasonBuffer = new StringBuffer();
        deletedReasonBuffer.append("selected reason: ");

        for(String selectedReason : deleteMemberDto.getSelectedReason()) {
            deletedReasonBuffer.append(selectedReason).append(" ");
        }

        deletedReasonBuffer.append("\n");
        deletedReasonBuffer.append("etcMessage: ");
        deletedReasonBuffer.append(deleteMemberDto.getEtcMsg());

        member.onDelete();

        member.registerReasonOfWithdrawal(deletedReasonBuffer.toString());
        memberRepository.delete(member);
    }

    @Transactional
    public void sendTemporaryPasswordAndUpdatePasswordToTemporary(String email) {
        Member member = memberRepository.findByEmailAndStatusIsActive(email)
            .orElseThrow(() -> new InvalidParamException("가입된 Email이 아닙니다."));

        if(! member.isActive()) {
            throw new InvalidParamException("유효하지 않은 이메일 입니다.");
        }

        String tempPassword = createRandomStrings();
        sendTemporaryPw(tempPassword, email);

        member.updatePwToTemporaryPW(passwordEncoder.encode(tempPassword));
    }

    @Transactional
    public void updatePasswordToNewPassword(PasswordUpdateDto updatePasswordDto) {
        Member member = memberRepository.findByEmailAndStatusIsActive(updatePasswordDto.getEmail())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EMAIL_NOT_EXIST));

        passwordMatchingCheck(updatePasswordDto.getPassword(), member.getPassword());

        String newEncodedPwd = passwordEncoder.encode(updatePasswordDto.getNewPassword());
        member.updateNewPassword(newEncodedPwd);
    }

    public boolean verifyMember(VerifyRequestDto verifyRequestDto) {
        if(StringUtils.isBlank(verifyRequestDto.getPassword())) {
            return memberRepository.existsMemberByEmail(verifyRequestDto.getEmail());
        } else {
            Member member = memberRepository.findByEmailAndStatusIsActive(verifyRequestDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EMAIL_NOT_EXIST));

            passwordMatchingCheck(verifyRequestDto.getPassword(), member.getPassword());
            return true;
        }
    }

    private String createRandomStrings() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for(int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤
            switch(index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        } return key.toString();
    }

    private void sendAuthCodeEmail(String email, String authCode) {
        MimeMessage message;
        try {
            message = createMessage(authCode, email);
        } catch(Exception e) {
            log.error(e.getMessage());
            Member storedMember = memberRepository.findByEmailAndStatusIs(email, Status.NEED_CERTIFICATED)
                .orElseThrow(RuntimeException::new);
            memberRepository.delete(storedMember);
            throw new RuntimeException(e);
        }

        javaMailSender.send(message);;
    }

    private void passwordMatchingCheck(String requestedPw, String storedPassword) {
        if (!this.passwordEncoder.matches(requestedPw, storedPassword)) {
            throw new BadCredentialsException("잘못된 패스워드 입니다.");
        }
    }

    private void sendTemporaryPw(String tempPw, String email) {
        MimeMessage message;
        try {
            message = createTemporaryPwFormat(tempPw, email);
        } catch(Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        javaMailSender.send(message);
    }

    private MimeMessage createMessage(String authCode, String to) throws Exception {
        final String template = "email/authcode";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setSubject("런챠 이메일 인증 코드입니다.");
        helper.setTo(to);
        helper.setCc("learcha@gmail.com");
        HashMap<String, String> emailValues = new HashMap<>();
        emailValues.put("code", authCode);
        Context context = new Context();
        emailValues.forEach(context::setVariable);
        String html = templateEngine.process(template, context);
        helper.setText(html, true);

        return message;
    }

    private MimeMessage createTemporaryPwFormat(String tempPw, String to) throws Exception {
        final String template = "email/password";
        if(StringUtils.isBlank(tempPw)) {
            throw new RuntimeException("Temp Password Arg is Null");
        }

        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        //메일 제목 설정
        helper.setSubject("런챠 임시 패스워드입니다.");
        helper.setTo(to);
        helper.setCc("learcha@gmail.com");
        HashMap<String, String> emailValues = new HashMap<>();
        emailValues.put("tempPassword", tempPw);
        Context context = new Context();
        emailValues.forEach(context::setVariable);
        String html = templateEngine.process(template, context);
        helper.setText(html, true);

        return message;
    }

    private MimeMessage createTemporaryPwFormat2(String tempPw, String to) throws Exception {
        if(StringUtils.isBlank(tempPw)) {
            throw new RuntimeException("Temp Password Arg is Null");
        }

        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(RecipientType.TO, to);//보내는 대상
        message.setSubject("임시 비밀번호 발송 테스트");//제목

        String msgg = ""; msgg += "<div style='margin:20px;'>";
        msgg += "<h1> 안녕하세요 learncha 입니다. </h1>"; msgg += "<br>"; msgg += "<p>아래 임시 비밀번호를 복사해 입력해주세요<p>";
        msgg += "<br>"; msgg += "<p>감사합니다.<p>"; msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>임시 비밀번호 입니다.</h3>";
        msgg += "<div style='font-size:130%'>"; msgg += "CODE : <strong>";
        msgg += tempPw + "</strong><div><br/> "; msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress(mailServerUsername, "learncha"));

        return message;
    }
}
