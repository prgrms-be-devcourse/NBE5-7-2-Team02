package io.twogether.nbe_5_7_2_02team.chat.service;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMemberRepository;
import io.twogether.nbe_5_7_2_02team.chat.dao.ChatRoomRepository;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatMemberGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.util.CheckUserLogin;
import io.twogether.nbe_5_7_2_02team.global.annotation.FlywayReset;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.domain.Role;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@FlywayReset
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ChatMemberServiceTest {

    @Autowired private ChatRoomService chatRoomService;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private ChatMemberService chatMemberService;

    @Autowired private PostRepository postRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ChatMemberRepository chatMemberRepository;

    @Autowired private CheckUserLogin checkUserLogin;

    UserDetails userDetails1;
    UserDetails userDetails2;
    Member member1;
    Member member2;
    Post post;

    ChatRoom chatRoom;
    Long chatRoomId;

    @BeforeEach
    void setUp() {
        chatMemberRepository.deleteAll();
        chatRoomRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();

        member1 =
                Member.builder()
                        .email("test1@example.com")
                        .name("testuser1")
                        .githubId("123")
                        .role(Role.MEMBER)
                        .build();
        member2 =
                Member.builder()
                        .email("test2@example.com")
                        .name("testuser2")
                        .githubId("456")
                        .role(Role.MEMBER)
                        .build();
        memberRepository.save(member1);
        memberRepository.save(member2);

        userDetails1 =
                User.builder()
                        .username(String.valueOf(member1.getId()))
                        .password("PASSWORD")
                        .authorities(Collections.emptyList())
                        .build();
        userDetails2 =
                User.builder()
                        .username(String.valueOf(member2.getId()))
                        .password("PASSWORD")
                        .authorities(Collections.emptyList())
                        .build();
        post =
                Post.builder()
                        .title("제목")
                        .content("내용")
                        .recruitmentStatus(RecruitmentStatus.NONE)
                        .build();

        postRepository.save(post);

        chatRoomId = chatRoomService.createChatroom(post.getId());
        chatRoom = chatRoomService.checkChatRoomExists(chatRoomId);
    }

    @Test
    @DisplayName("채팅방 멤버 목록 조회 테스트: 성공")
    void getChatMemberTest() {
        chatMemberService.createChatMember(chatRoomId, userDetails1);
        chatMemberService.createChatMember(chatRoomId, userDetails2);

        List<ChatMemberGetResponse> chatMemberGetResponseList =
                chatMemberService.getChatMember(chatRoomId);

        System.out.println("========================================");
        for (ChatMemberGetResponse chatMemberGetResponse : chatMemberGetResponseList) {
            System.out.println(
                    "chatMemberResponse MemberId: " + chatMemberGetResponse.getMemberId());
            System.out.println(
                    "chatMemberResponse MemberName: " + chatMemberGetResponse.getMemberName());
        }
        System.out.println("========================================");
    }

    @Test
    @DisplayName("채팅방 멤버 목록 조회 테스트: 에러 - 채팅방을 찾을 수 없음")
    void getChatMemberNotFoundChatRoomTest() {
        chatRoomRepository.deleteById(chatRoomId);

        ErrorException errorException =
                assertThrows(
                        ErrorException.class, () -> chatMemberService.getChatMember(chatRoomId));

        assertEquals(CHAT_ROOM_NOT_FOUND, errorException.getErrorCode());
    }

    @Test
    @DisplayName("채팅방 멤버 목록 조회 테스트: 에러 - 참여 중인 멤버 없음")
    void getChatMemberEmptyMemberTest() {
        chatMemberRepository.deleteById(chatRoomId);

        ErrorException errorException =
                assertThrows(
                        ErrorException.class, () -> chatMemberService.getChatMember(chatRoomId));

        assertEquals(CHAT_ROOM_EMPTY, errorException.getErrorCode());
    }

    @Test
    @DisplayName("채팅방 입장 테스트: 성공")
    void createChatMemberTest() {
        chatMemberService.createChatMember(chatRoomId, userDetails1);

        Member member = checkUserLogin.checkUserLogin(userDetails1);

        ChatMember chatMember = chatMemberRepository.findByChatRoomAndMember(chatRoom, member);

        System.out.println("========================================");
        System.out.println("ChatRoomId: " + chatRoom.getId());
        System.out.println("memberId: " + member.getId());
        System.out.println("========================================");
        System.out.println("chatMemberId: " + chatMember.getId());
        System.out.println("chatMemberChatRoomId: " + chatMember.getChatRoom().getId());
        System.out.println("chatMemberMemberId: " + chatMember.getMember().getId());
        System.out.println("chatMemberStatus: " + chatMember.getChatMemberStatus());
        System.out.println("chatMemberCreatedAt: " + chatMember.getCreatedAt());
        System.out.println("========================================");
    }

    @Test
    @DisplayName("채팅방 입장 테스트: 에러 - 비로그인 유저")
    void createChatMemberNotLoginTest() {

        ErrorException errorException =
                assertThrows(
                        ErrorException.class,
                        () -> chatMemberService.createChatMember(chatRoomId, null));

        assertEquals(CHAT_MEMBER_NOT_LOGIN, errorException.getErrorCode());
    }

    @Test
    @DisplayName("채팅방 입장 테스트: 에러 - 채팅방이 없음")
    void createChatMemberNoChatRoomTest() {
        chatRoomRepository.deleteAll();

        ErrorException errorException =
                assertThrows(
                        ErrorException.class,
                        () -> chatMemberService.createChatMember(chatRoomId, userDetails1));

        assertEquals(CHAT_ROOM_NOT_FOUND, errorException.getErrorCode());
    }

    @Test
    @DisplayName("멤버 상태 변경 테스트: 성공")
    void updateChatMemberTest() {
        chatMemberService.createChatMember(chatRoomId, userDetails1);

        chatMemberService.updateChatMember(chatRoomId, userDetails1, ChatMemberStatus.LEFT);
        chatMemberService.updateChatMember(chatRoomId, userDetails1, ChatMemberStatus.ONLINE);
        chatMemberService.updateChatMember(chatRoomId, userDetails1, ChatMemberStatus.OFFLINE);
    }

    @Test
    @DisplayName("멤버 상태 변경 테스트: 에러 - 비로그인")
    void updateChatMemberNotLoginTest() {

        ErrorException errorException =
                assertThrows(
                        ErrorException.class,
                        () ->
                                chatMemberService.updateChatMember(
                                        chatRoomId, null, ChatMemberStatus.ONLINE));

        assertEquals(CHAT_MEMBER_NOT_LOGIN, errorException.getErrorCode());
    }

    @Test
    @DisplayName("멤버 상태 변경 테스트: 에러 - 채팅방이 없음")
    void updateChatMemberNotFoundChatRoomTest() {
        chatRoomRepository.deleteAll();

        ErrorException errorException =
                assertThrows(
                        ErrorException.class,
                        () ->
                                chatMemberService.updateChatMember(
                                        chatRoomId, userDetails1, ChatMemberStatus.ONLINE));

        assertEquals(CHAT_ROOM_NOT_FOUND, errorException.getErrorCode());
    }

    @Test
    @DisplayName("멤버 상태 변경 테스트: 에러 - 참여중이지 않음")
    void updateChatMemberJoinChatRoomTest() {
        chatMemberRepository.deleteAll();

        ErrorException errorException =
                assertThrows(
                        ErrorException.class,
                        () ->
                                chatMemberService.updateChatMember(
                                        chatRoomId, userDetails1, ChatMemberStatus.ONLINE));

        assertEquals(CHAT_MEMBER_NOT_ENTER, errorException.getErrorCode());
    }
}
