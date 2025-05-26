package io.twogether.nbe_5_7_2_02team.oauth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GitHubLoginResponse {

    String accessToken;
}
