package io.twogether.nbe_5_7_2_02team.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@FlywayReset
public class MemberBrowserSuccessTest extends BrowserTestTemplate {

    @Autowired MemberRepository memberRepository;

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("GET: /api/member/me - 자기 정보 조회 성공")
    void getMyProfileSuccess() throws Exception {

        long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow();
        TokenPair tokenPair = jwtTokenProvider.generateTokenPair(member);

        mockMvc.perform(
                        get("/api/member/me")
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId))
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.email").value(member.getEmail()));
    }

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("GET: /api/member/{id} - 타인 정보 조회 성공")
    void getOtherMemberProfileSuccess() throws Exception {

        long loginMemberId = 1L;
        long targetMemberId = 2L;

        Member loginMember = memberRepository.findById(loginMemberId).orElseThrow();
        Member targetMember = memberRepository.findById(targetMemberId).orElseThrow();

        TokenPair tokenPair = jwtTokenProvider.generateTokenPair(loginMember);

        mockMvc.perform(
                        get("/api/member/" + targetMemberId)
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(targetMemberId))
                .andExpect(jsonPath("$.name").value(targetMember.getName()))
                .andExpect(jsonPath("$.email").value(targetMember.getEmail()));
    }

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("PATCH: /api/member/me - 본인 닉네임과 이미지 수정 성공")
    void updateMyProfileSuccess() throws Exception {

        long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow();
        TokenPair tokenPair = jwtTokenProvider.generateTokenPair(member);

        String newNickName = "newNickName";
        String imageUrl = "newImageUrl";
        MockMultipartFile newImage =
                new MockMultipartFile("image", "image.png", "image/png", "image".getBytes());

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/api/member/me")
                                .file(newImage)
                                .param("nickname", newNickName)
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(newNickName))
                .andExpect(jsonPath("$.image").isNotEmpty())
                .andExpect(jsonPath("$.image").value(imageUrl));
    }
}
