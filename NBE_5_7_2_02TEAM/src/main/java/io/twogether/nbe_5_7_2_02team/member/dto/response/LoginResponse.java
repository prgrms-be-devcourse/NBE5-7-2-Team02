package io.twogether.nbe_5_7_2_02team.member.dto.response;

import io.twogether.nbe_5_7_2_02team.member.domain.Role;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponse {
    private TokenPair tokenPair;
    private Role role;
    private Long memberId;
}
