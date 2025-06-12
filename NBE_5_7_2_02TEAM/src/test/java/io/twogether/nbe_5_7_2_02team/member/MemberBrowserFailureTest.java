package io.twogether.nbe_5_7_2_02team.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.database.rider.core.api.dataset.DataSet;

import io.twogether.nbe_5_7_2_02team.browser.template.BrowserTestTemplate;
import io.twogether.nbe_5_7_2_02team.global.annotation.FlywayReset;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;

@FlywayReset
public class MemberBrowserFailureTest extends BrowserTestTemplate {

    @Autowired MemberRepository memberRepository;

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("GET: /api/member/me - 인증 토큰 없음")
    void getMyProfile_Unauthorized() throws Exception {

        mockMvc.perform(get("/api/member/me"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/oauth2/authorization/github"));
    }

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("GET: /api/member/{id} - 존재하지 않는 회원 ID 조회")
    void getOtherProfile_NotFound() throws Exception {

        long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow();
        TokenPair tokenPair = jwtTokenProvider.generateTokenPair(member);

        long invalidId = 9999L;

        mockMvc.perform(
                        get("/api/member/" + invalidId)
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isNotFound()); // 404
    }

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("PATCH: /api/member/me - 인증 토큰 없음")
    void updateMyProfile_Unauthorized() throws Exception {
        MockMultipartFile image =
                new MockMultipartFile("image", "a.png", "image/png", "dummy".getBytes());

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/api/member/me")
                                .file(image)
                                .param("nickname", "memberNickname"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/oauth2/authorization/github"));
    }

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("PATCH: /api/member/me - 닉네임 누락 (공백)")
    void updateMyProfile_NicknameBlank() throws Exception {

        long memberId = 1L;

        Member member = memberRepository.findById(memberId).orElseThrow();
        TokenPair tokenPair = jwtTokenProvider.generateTokenPair(member);

        MockMultipartFile image =
                new MockMultipartFile("image", "a.png", "image/png", "dummy".getBytes());

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/api/member/me")
                                .file(image)
                                .param("nickname", " ") // 공백
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isBadRequest());
    }
}
