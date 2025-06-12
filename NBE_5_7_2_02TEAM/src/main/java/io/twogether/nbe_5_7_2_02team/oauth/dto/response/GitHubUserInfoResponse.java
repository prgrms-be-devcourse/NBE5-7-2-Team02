package io.twogether.nbe_5_7_2_02team.oauth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Builder
public class GitHubUserInfoResponse {
    private String githubId;
    private String email;
    private String avatarUrl;
    private List<String> organizations;
}
