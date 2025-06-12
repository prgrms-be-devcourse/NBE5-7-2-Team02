package io.twogether.nbe_5_7_2_02team.oauth.service;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.domain.Role;
import io.twogether.nbe_5_7_2_02team.member.dto.request.SignUpRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.response.LoginResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.response.SignUpResponse;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.MemberDetails;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;
import io.twogether.nbe_5_7_2_02team.oauth.dto.response.GitHubLoginResponse;
import io.twogether.nbe_5_7_2_02team.oauth.dto.response.GitHubUserInfoResponse;
import io.twogether.nbe_5_7_2_02team.oauth.jwt.JwtTokenProvider;
import io.twogether.nbe_5_7_2_02team.oauth.jwt.MemberDetailsFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuthService extends DefaultOAuth2UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User = {}", oAuth2User);
        LoginResponse loginResponse = login(userRequest.getAccessToken().getTokenValue());

        String providerId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        MemberDetails memberDetails = MemberDetailsFactory.memberDetails(providerId, oAuth2User);

        Optional<Member> memberOptional = memberRepository.findById(loginResponse.getMemberId());

        return memberDetails
                .setId(memberOptional.get().getId())
                .setRole(memberOptional.get().getRole());
    }

    // GitHub에서 받은 code로 GitHub의 AccessToken 발급
    public GitHubLoginResponse getAccessToken(String code) {
        String accessTokenUrl = "https://github.com/login/oauth/access_token";

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(accessTokenUrl)
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<JSONObject> response =
                    restTemplate.exchange(
                            builder.toUriString(), HttpMethod.POST, entity, JSONObject.class);

            JSONObject body = response.getBody();
            if (body != null && body.containsKey("access_token")) {
                log.info("GitHub access token obtained successfully");
                return GitHubLoginResponse.builder()
                        .accessToken(body.getAsString("access_token"))
                        .build();
            }

            log.error("GitHub token response missing access_token: {}", body);
            throw new ErrorException(ErrorCode.OAUTH_TOKEN_ERROR);
        } catch (Exception e) {
            log.error("Failed to get GitHub access token", e);
            throw new ErrorException(ErrorCode.OAUTH_TOKEN_ERROR);
        }
    }

    // GitHub의 AccessToken을 이용하여 GitHub 사용자 정보 조회
    public GitHubUserInfoResponse getUserInfo(String accessToken) {
        HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders(accessToken));

        JSONObject userInfo = getForObject("https://api.github.com/user", entity);
        if (userInfo == null) {
            throw new ErrorException(ErrorCode.OAUTH_USER_INFO_ERROR);
        }

        // GitHub 사용자 ID
        String login = userInfo.getAsString("login");
        // GitHub 프로필 이미지 url
        String avatarUrl = userInfo.getAsString("avatar_url");
        String email = fetchPrimaryEmail(entity);
        List<String> organizations = fetchOrganizations(entity);

        return GitHubUserInfoResponse.builder()
                .githubId(login)
                .avatarUrl(avatarUrl)
                .email(email)
                .organizations(organizations)
                .build();
    }

    // GitHub 사용자의 기본 이메일
    private String fetchPrimaryEmail(HttpEntity<String> entity) {
        ResponseEntity<Object[]> response =
                restTemplate.exchange(
                        "https://api.github.com/user/emails",
                        HttpMethod.GET,
                        entity,
                        Object[].class);

        Object[] emails = response.getBody();
        if (emails != null) {
            for (Object emailObj : emails) {
                if (emailObj instanceof Map<?, ?> emailMap) {
                    boolean primary = Boolean.TRUE.equals(emailMap.get("primary"));
                    boolean verified = Boolean.TRUE.equals(emailMap.get("verified"));
                    if (primary && verified && emailMap.get("email") != null) {
                        return emailMap.get("email").toString();
                    }
                }
            }
        }

        throw new ErrorException(ErrorCode.OAUTH_EMAIL_NOT_FOUND);
    }

    // GitHub 사용자의 organization 목록
    private List<String> fetchOrganizations(HttpEntity<String> entity) {
        ResponseEntity<Object[]> response =
                restTemplate.exchange(
                        "https://api.github.com/user/orgs", HttpMethod.GET, entity, Object[].class);

        List<String> organizations = new ArrayList<>();
        Object[] orgs = response.getBody();

        if (orgs != null) {
            for (Object orgObj : orgs) {
                if (orgObj instanceof Map<?, ?> orgMap && orgMap.get("login") != null) {
                    organizations.add(orgMap.get("login").toString());
                }
            }
        }

        return organizations;
    }

    // GitHub에서 가져온 사용자 정보를 DB에 저장
    public Member saveUserInfo(GitHubUserInfoResponse userInfo) {
        validatePrgrmsOrganization(userInfo.getOrganizations());

        Member member =
                Member.builder()
                        .githubId(userInfo.getGithubId())
                        .email(userInfo.getEmail())
                        .profileImage(userInfo.getAvatarUrl())
                        .role(Role.MEMBER)
                        .build();

        return memberRepository.save(member);
    }

    // GitHub의 Access Token으로 로그인 (첫 로그인 시 정보 저장)
    public LoginResponse login(String accessToken) {
        GitHubUserInfoResponse userInfo = getUserInfo(accessToken);
        Member member =
                memberRepository
                        .findByEmail(userInfo.getEmail())
                        .orElseGet(() -> saveUserInfo(userInfo));
        TokenPair tokenPair = jwtTokenProvider.generateTokenPair(member);
        return LoginResponse.builder()
                .tokenPair(tokenPair)
                .role(member.getRole())
                .memberId(member.getId())
                .build();
    }

    // 추가 회원 가입 정보 등록
    public SignUpResponse signup(SignUpRequest request, Long id) {
        Member member =
                memberRepository
                        .findById(id)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        member.setName(request.getName());
        member.setJob(request.getJob());
        member.setCourse(request.getCourse());

        return SignUpResponse.from(memberRepository.save(member));
    }

    private static final Set<String> ALLOWED_ORGS =
            Set.of(
                    "prgrms-web-devcourse",
                    "prgrms-be-devcourse",
                    "prgrms-fe-devcourse",
                    "prgrms-ad-devcourse",
                    "prgrms-aibe-devcourse",
                    "prgrms-app-devcourse",
                    "prgrms-linux-devcourse",
                    "prgrms-fullcycle-devcourse");

    private void validatePrgrmsOrganization(List<String> organizations) {
        boolean hasPrgrms =
                organizations.stream().map(String::toLowerCase).anyMatch(ALLOWED_ORGS::contains);

        if (!hasPrgrms) {
            throw new OAuth2AuthenticationException("프로그래머스 교육 과정에 등록된 사용자만 가입할 수 있습니다.");
        }
    }

    public MemberDetails getMemberDetailsById(Long id) {
        Member member =
                memberRepository
                        .findById(id)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));
        return MemberDetails.from(member);
    }

    // 인증 헤더 생성
    private HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "token " + accessToken);
        return headers;
    }

    // 요청 URL에서 JSON 객체 받기
    private JSONObject getForObject(String url, HttpEntity<String> entity) {
        ResponseEntity<JSONObject> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, JSONObject.class);
        return response.getBody();
    }
}
