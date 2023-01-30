package com.learncha.api.auth.service;

import com.learncha.api.auth.domain.Member;
import com.learncha.api.auth.domain.Member.AuthType;
import com.learncha.api.auth.domain.Member.Status;
import com.learncha.api.auth.repository.MemberRepository;
import com.learncha.api.auth.web.AuthDto.SignUpRequest;
import com.learncha.api.auth.web.AuthDto.SignUpResponse;
import com.learncha.api.common.exception.AlreadyAuthenticatedEmail;
import com.learncha.api.common.exception.InvalidParamException;
import java.util.Random;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Value("spring.mail.username")
    private String email;

    @Transactional
    public SignUpResponse signUpMember(SignUpRequest signUpRequest) {
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        String email = signUpRequest.getEmail();

        Member initMember = memberRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidParamException("해당 Email로 인증된 사용자가 없습니다."));

        Member authenticatedMember = initMember.updateEmailAuthenticatedUser(signUpRequest,
            encodedPassword);

        memberRepository.save(authenticatedMember);

        return SignUpResponse.builder().email(email)
            .memberToken(authenticatedMember.getMemberToken())
            .authType(authenticatedMember.getAuthType().getDescription()).build();
    }

    public void emailAuthentication(String email) {
        memberRepository.findByEmail(email)
            .ifPresentOrElse(
                member -> {
                    String memberStatus = member.getStatus().getDescription();
                    if(memberStatus.equals(Status.AUTHENTICATED.getDescription())) {
                        throw new AlreadyAuthenticatedEmail();
                    } else if(memberStatus.equals(Status.NEED_AUTHENTICATED.getDescription())) {
                        Member reMember = reSendAuthCodeToEmail(member, email);
                        memberRepository.save(reMember);
                    }

                },
                () -> {
                    Member member = sendFirstAuthCodeToEmail(email);
                    memberRepository.save(member);
                });
    }

    private Member sendFirstAuthCodeToEmail(String email) {
        Member initMember = Member.createInitEmailMember(email, AuthType.EMAIL);
        MimeMessage message = null;

        try {
            message = createMessage(initMember, email);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        javaMailSender.send(message); return initMember;
    }

    private Member reSendAuthCodeToEmail(Member member, String email) {
        MimeMessage message = null;

        try {
            message = createMessage(member, email);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        javaMailSender.send(message);
        return member;
    }

    public boolean getAuthResult(String authCode, String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(InvalidParamException::new);

        String savedAuthCode = member.getAuthenticationCode();

        if(savedAuthCode.equals(authCode))
            member.emailAuthenticationSuccess();

        memberRepository.save(member);
        return savedAuthCode.equals(authCode);
    }

    public boolean isAvailableEmail(String email) {
        boolean res = memberRepository.existsMemberByEmail(email); if(! res) {
            Member member = Member.createInitEmailMember(email, AuthType.EMAIL);
            memberRepository.save(member);
        }

        return res;
    }

    private String createAuthCode() {
        StringBuffer key = new StringBuffer(); Random rnd = new Random();

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

    private MimeMessage createMessage(Member member, String to) throws Exception {
        String authCode = createAuthCode(); member.setAuthenticationCode(authCode);

        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(RecipientType.TO, to);//보내는 대상
        message.setSubject("이메일 인증 테스트");//제목

        String msgg = ""; msgg += "<div style='margin:20px;'>";
        msgg += "<h1> 안녕하세요 learncha 입니다. </h1>"; msgg += "<br>"; msgg += "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg += "<br>"; msgg += "<p>감사합니다.<p>"; msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>"; msgg += "CODE : <strong>";
        msgg += authCode + "</strong><div><br/> "; msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress(email, "learncha"));

        return message;
    }
}
