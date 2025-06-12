package io.twogether.nbe_5_7_2_02team.oauth.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;

import io.twogether.nbe_5_7_2_02team.browser.template.BrowserTestTemplate;
import io.twogether.nbe_5_7_2_02team.global.annotation.FlywayReset;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.dto.request.SignUpRequest;
import io.twogether.nbe_5_7_2_02team.oauth.dao.RefreshTokenBlackListRepository;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;
import io.twogether.nbe_5_7_2_02team.oauth.dto.request.LogoutRequest;
import io.twogether.nbe_5_7_2_02team.oauth.dto.request.RefreshRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@FlywayReset
public class TokenBrowserSuccessTest extends BrowserTestTemplate {

    @Autowired MemberRepository memberRepository;
    @Autowired ObjectMapper objectMapper;
    @Autowired RefreshTokenBlackListRepository refreshTokenBlackListRepository;

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("POST: /api/token/refresh 리프레시 토큰을 이용해 토큰 재발급")
    void refreshToken() throws Exception {
        // given
        TokenPair tokenPair = genTokenPair(1L);
        RefreshRequest request = new RefreshRequest(tokenPair.getRefreshToken());

        // when & then
        mockMvc.perform(
                        post("/api/token/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(tokenPair.getAccessToken()))
                .andExpect(jsonPath("$.refresh_token").value(tokenPair.getRefreshToken()));
    }

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("POST: /api/logout 로그아웃 요청 성공")
    void logout() throws Exception {
        // given
        TokenPair tokenPair = genTokenPair(1L);
        LogoutRequest request = new LogoutRequest(tokenPair.getRefreshToken());

        // when & then
        mockMvc.perform(
                        post("/api/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("POST: /api/signup 회원가입 성공")
    void signup() throws Exception {
        // given
        long memberId = 1L;
        TokenPair tokenPair = genTokenPair(memberId);

        SignUpRequest request =
                SignUpRequest.builder().name("신규가입자").job("DEVELOPER").course("SPRING").build();

        // when & then
        mockMvc.perform(
                        post("/api/signup")
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(memberId))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.job").value(request.getJob()))
                .andExpect(jsonPath("$.course").value(request.getCourse()));
    }

    private TokenPair genTokenPair(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        return jwtTokenProvider.generateTokenPair(member);
    }
}
