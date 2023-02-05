package com.learncha.api.auth.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    GOOGLE("google", (attributes) -> new GoogleUserProfile(
        String.valueOf(attributes.get("sub")),
        (String) attributes.get("email"),
        (String) attributes.get("given_name"),
        (String) attributes.get("family_name"),
        (String) attributes.get("picture")
    ));

    private final String registrationId;
    private final Function<Map<String, Object>, GoogleUserProfile> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, GoogleUserProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static GoogleUserProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
            .filter(provider -> registrationId.equals(provider.registrationId))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new)
            .of.apply(attributes);
    }
}
