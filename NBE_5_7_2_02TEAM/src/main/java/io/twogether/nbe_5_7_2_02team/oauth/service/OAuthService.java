package io.twogether.nbe_5_7_2_02team.oauth.service;

import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.oauth.dto.MemberDetails;
import io.twogether.nbe_5_7_2_02team.oauth.jwt.MemberDetailsFactory;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuthService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User = {}", oAuth2User);

        String providerId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        MemberDetails memberDetails = MemberDetailsFactory.memberDetails(providerId, oAuth2User);

        Optional<Member> memberOptional = memberRepository.findByGithubId(
            memberDetails.getGithubId());

        Member findMember = memberOptional.orElseGet(() -> {
            Member member = Member.builder()
                .name(memberDetails.getName())
                .githubId(memberDetails.getGithubId())
                .build();
            return memberRepository.save(member);
        });

        return memberDetails.setId(findMember.getId()).setRole(findMember.getRole());

    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Member getById(Long id) {
        return findById(id)
            .orElseThrow(
                () -> new NoSuchElementException()
            );
    }

    public MemberDetails getMemberDetailsById(Long id) {
        Member findMember = getById(id);
        return MemberDetails.from(findMember);
    }


}

