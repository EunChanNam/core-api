package com.learncha.api.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GoogleUserProfile {
    private final String oauthId;
    private final String email;
    private final String givenName;
    private final String familyName;
    private final String imageUrl;
}
