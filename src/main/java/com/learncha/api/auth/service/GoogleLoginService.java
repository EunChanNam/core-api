package com.learncha.api.auth.service;

import com.learncha.api.auth.domain.GoogleUserProfile;
import com.learncha.api.auth.domain.Member;
import com.learncha.api.auth.domain.OAuthAttributes;
import com.learncha.api.auth.repository.MemberRepository;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleLoginService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration()
            .getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        GoogleUserProfile userProfile = OAuthAttributes.extract(registrationId, attributes); // registrationId에 따라 유저 정보를 통해 공통된 UserProfile 객체로 만들어 줌

        Member member = saveOrUpdate(userProfile); // DB에 저장

        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().getDescription())),
            attributes,
            userNameAttributeName
        );
    }

    private Member saveOrUpdate(GoogleUserProfile googleUserProfile) throws OAuth2AuthenticationException {
        Member member = memberRepository.findByEmail(googleUserProfile.getEmail())
            .map(storedMember-> storedMember.updateMemberInfoFromGoogle(googleUserProfile))
            .orElse(Member.createGoogleAuthMember(googleUserProfile));

        return memberRepository.save(member);
    }
}
