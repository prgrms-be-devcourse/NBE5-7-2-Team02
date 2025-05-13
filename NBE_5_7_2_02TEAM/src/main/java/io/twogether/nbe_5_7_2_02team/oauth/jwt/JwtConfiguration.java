package io.twogether.nbe_5_7_2_02team.oauth.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "custom.jwt")
public class JwtConfiguration {

    private final Validation validation;
    private final Secrets secrets;

    @Getter
    @RequiredArgsConstructor
    public static class Validation {
        private final Long access;
        private final Long refresh;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Secrets {
        private final String originKey;
        private final String appKey;
    }
}
