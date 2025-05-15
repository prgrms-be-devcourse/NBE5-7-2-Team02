package io.twogether.nbe_5_7_2_02team.chat.util;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_NOT_LOGIN;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_FOUND_MEMBER;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckUserLogin {

    private final MemberRepository memberRepository;

    public Member checkUserLogin(UserDetails userDetails) {
        if (userDetails == null) {
            throw new ErrorException(CHAT_MEMBER_NOT_LOGIN);
        }

        return memberRepository
                .findById(Long.parseLong(userDetails.getUsername()))
                .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));
    }
}
