package io.twogether.nbe_5_7_2_02team.oauth.dto;

import io.twogether.nbe_5_7_2_02team.member.domain.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenBody {
    private Long memberId;
    private Role role;
}
