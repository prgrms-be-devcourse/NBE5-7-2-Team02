package io.twogether.nbe_5_7_2_02team.oauth.jwt;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorCode;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.oauth.dto.MemberDetails;
import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class MemberDetailsFactory {

    public static MemberDetails memberDetails(String provider, OAuth2User oAuth2User) {

        Map<String, Object> attributes = oAuth2User.getAttributes();

        switch ( provider.toUpperCase().trim() ) {

            case "GITHUB" -> {
                return MemberDetails.builder()
                    .name(attributes.get("name").toString())
                    .githubId(attributes.get("githubId").toString())
                    .attributes(attributes)
                    .build();
            }

            default -> throw new ErrorException(ErrorCode.UNSUPPORTED_PROVIDER);

        }

    }

}
