package io.twogether.nbe_5_7_2_02team.oauth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    private String refreshToken;
}
