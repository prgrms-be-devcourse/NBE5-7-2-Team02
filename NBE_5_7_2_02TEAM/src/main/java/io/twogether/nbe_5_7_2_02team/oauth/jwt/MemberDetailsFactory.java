package io.twogether.nbe_5_7_2_02team.oauth.jwt;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.MemberDetails;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class MemberDetailsFactory {

    public static MemberDetails memberDetails(String provider, OAuth2User oAuth2User) {

        Map<String, Object> attributes = oAuth2User.getAttributes();

        switch (provider.toUpperCase().trim()) {
            case "GITHUB" -> {
                return MemberDetails.builder()
                        .name(attributes.get("login").toString())
                        .attributes(attributes)
                        .build();
            }

            default -> throw new ErrorException(ErrorCode.UNSUPPORTED_PROVIDER);
        }
    }
}
