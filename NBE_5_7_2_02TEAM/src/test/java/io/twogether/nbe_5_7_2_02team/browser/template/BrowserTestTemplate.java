package io.twogether.nbe_5_7_2_02team.browser.template;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.twogether.nbe_5_7_2_02team.browser.config.MockTestConfig;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.domain.Role;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;
import io.twogether.nbe_5_7_2_02team.oauth.jwt.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@Import(MockTestConfig.class)
public abstract class BrowserTestTemplate {

    @Autowired public ObjectMapper objectMapper;
    @Autowired public MockMvc mockMvc;
    @Autowired public MemberRepository memberRepository;
    @Autowired public JwtTokenProvider jwtTokenProvider;

    public Member member;
    public TokenPair tokenPair;

    @BeforeEach
    public void memberSetup() {
        memberRepository.save(
                member =
                        Member.builder()
                                .name("TEST_MEMBER")
                                .email("TEST_MEMBER@example.com")
                                .githubId("github.com")
                                .profileImage("TEST_IMAGE")
                                .role(Role.MEMBER)
                                .build());
        tokenPair = jwtTokenProvider.generateTokenPair(member);
    }
}
