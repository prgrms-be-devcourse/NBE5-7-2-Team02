// package io.twogether.nbe_5_7_2_02team.oauth.service;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.doReturn;
// import static org.mockito.Mockito.when;
//
// import io.twogether.nbe_5_7_2_02team.browser.template.BrowserTestTemplate;
// import io.twogether.nbe_5_7_2_02team.global.annotation.FlywayReset;
// import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
// import io.twogether.nbe_5_7_2_02team.member.domain.Member;
// import io.twogether.nbe_5_7_2_02team.member.domain.Role;
// import io.twogether.nbe_5_7_2_02team.member.dto.response.LoginResponse;
// import io.twogether.nbe_5_7_2_02team.oauth.dto.response.GitHubUserInfoResponse;
// import io.twogether.nbe_5_7_2_02team.oauth.jwt.JwtTokenProvider;
// import java.util.List;
// import java.util.Optional;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.client.RestTemplate;
// import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;
//
//
// @FlywayReset
// @SpringBootTest
// @Transactional
// class OAuthServiceTest extends BrowserTestTemplate {
//
//    @Autowired
//    JwtTokenProvider jwtTokenProvider;
//    @Autowired
//    MemberRepository memberRepository;
//    @Autowired
//    OAuthService oAuthService;
//
//    private String dummyAccessToken = "dummyAccessToken";
//
//    private GitHubUserInfoResponse dummyUserInfo;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @BeforeEach
//    void setUp() {
//        dummyUserInfo = GitHubUserInfoResponse.builder()
//            .githubId("testGithubId")
//            .githubId("testGithubId")
//            .avatarUrl("http://avatar.url")
//            .email("test@example.com")
//            .organizations(List.of("prgrms-web-devcourse"))
//            .build();
//    }
//
//    @Test
//    @DisplayName("")
//    void first_login_success() throws Exception {
//
//        when(oAuthService.getUserInfo(dummyAccessToken)).thenReturn(dummyUserInfo);
//
//        LoginResponse loginResponse = oAuthService.login(dummyAccessToken);
//
//        assertNotNull(loginResponse);
//        assertNotNull(loginResponse.getTokenPair());
//        assertEquals(Role.MEMBER, loginResponse.getRole());
//
//        Optional<Member> savedMember = memberRepository.findByEmail(dummyUserInfo.getEmail());
//        assertThat(savedMember).isPresent();
//        assertThat(savedMember.get().getGithubId()).isEqualTo("testGithubId");
//
//    }
//
// }
