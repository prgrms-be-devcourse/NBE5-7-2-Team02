package io.twogether.nbe_5_7_2_02team.member.dto;

import io.twogether.nbe_5_7_2_02team.member.domain.Role;

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
    private String accessToken;
    private String refreshToken;
    private Role role;
}
