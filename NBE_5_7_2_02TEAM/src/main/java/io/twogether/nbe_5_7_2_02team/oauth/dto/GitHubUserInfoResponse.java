package io.twogether.nbe_5_7_2_02team.oauth.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class GitHubUserInfoResponse {
    private String githubId;
    private String email;
    private String avatarUrl;
    private List<String> organizations;

}
