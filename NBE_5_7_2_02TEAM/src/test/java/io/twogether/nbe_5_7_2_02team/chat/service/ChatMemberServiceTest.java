package io.twogether.nbe_5_7_2_02team.chat.service;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_ALREADY_EXISTS;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_NOT_LOGIN;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_EMPTY;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_NOT_FOUND;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMemberRepository;
import io.twogether.nbe_5_7_2_02team.chat.dao.ChatRoomRepository;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.chat.domain.Status;
import io.twogether.nbe_5_7_2_02team.chat.dto.ChatMemberResponse;
import io.twogether.nbe_5_7_2_02team.chat.util.CheckUserLogin;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.domain.Role;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "1", password = "<PASSWORD>")
class ChatMemberServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatMemberService chatMemberService;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    UserDetails userDetails1 = User.builder()
        .username("1")
        .password("PASSWORD")
        .authorities(Collections.emptyList())
        .build();
    UserDetails userDetails2 = User.builder()
        .username("2")
        .password("PASSWORD")
        .authorities(Collections.emptyList())
        .build();
    Member member = Member.builder()
        .email("test1@example.com")
        .name("testuser1")
        .githubId("123")
        .role(Role.MEMBER)
        .build();
    Member member2 = Member.builder()
        .email("test2@example.com")
        .name("testuser2")
        .githubId("456")
        .role(Role.MEMBER)
        .build();
    Post post= Post.builder()
        .title("제목")
        .content("내용")
        .recruitmentStatus(RecruitmentStatus.NONE).build();

    ChatRoom chatRoom;
    Long chatRoomId;


    @Autowired
    private CheckUserLogin checkUserLogin;
    @Autowired
    private ChatMemberRepository chatMemberRepository;


    @BeforeEach
    void setUp() {
        chatMemberRepository.deleteAll();
        chatRoomRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();

        memberRepository.save(member);
        memberRepository.save(member2);
        postRepository.save(post);

        chatRoomId = chatRoomService.createChatroom(post.getId());
        chatRoom = chatRoomService.checkChatRoomExists(chatRoomId);
    }

    @Test
    @DisplayName("채팅방 멤버 목록 조회 테스트: 성공")
    void getChatMemberTest() {
        chatMemberService.createChatMember(chatRoomId, userDetails1);
        chatMemberService.createChatMember(chatRoomId, userDetails2);

        List<ChatMemberResponse> chatMemberResponseList = chatMemberService.getChatMember(chatRoomId);

        System.out.println("========================================");
        for (ChatMemberResponse chatMemberResponse : chatMemberResponseList) {
            System.out.println("chatMemberResponse ChatroomId: " + chatMemberResponse.getChatroomId());
            System.out.println("chatMemberResponse MemberId: " + chatMemberResponse.getMemberId());
            System.out.println("chatMemberResponse MemberName: " + chatMemberResponse.getMemberName());
            System.out.println("chatMemberResponse CreatedAt: " + chatMemberResponse.getCreatedAt());
        }
        System.out.println("========================================");

    }

    @Test
    @DisplayName("채팅방 멤버 목록 조회 테스트: 에러 - 채팅방을 찾을 수 없음")
    void getChatMemberNotFoundChatRoomTest() {
        chatRoomRepository.deleteById(chatRoomId);

        try {
            chatMemberService.getChatMember(chatRoomId);
        } catch (ErrorException e) {
            if (e.getErrorCode() == CHAT_ROOM_NOT_FOUND) {
                System.out.println("========================================");
                System.out.println("CHAT_ROOM_NOT_FOUND 발생");
                System.out.println("========================================");
            }
        }
    }

    @Test
    @DisplayName("채팅방 멤버 목록 조회 테스트: 에러 - 참여 중인 멤버 없음")
    void getChatMemberEmptyMemberTest() {
        chatMemberRepository.deleteById(chatRoomId);

        try {
            chatMemberService.getChatMember(chatRoomId);
        } catch (ErrorException e) {
            if (e.getErrorCode() == CHAT_ROOM_EMPTY) {
                System.out.println("========================================");
                System.out.println("CHAT_ROOM_EMPTY 발생");
                System.out.println("========================================");
            }
        }
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
        System.out.println("chatMemberStatus: " + chatMember.getStatus());
        System.out.println("chatMemberCreatedAt: " + chatMember.getCreatedAt());
        System.out.println("========================================");
    }

    @Test
    @DisplayName("채팅방 입장 테스트: 에러 - 비로그인 유저")
    void createChatMemberNotLoginTest() {
        try {
            chatMemberService.createChatMember(chatRoomId, null);
        } catch (ErrorException e) {
            if (e.getErrorCode() == CHAT_MEMBER_NOT_LOGIN) {
                System.out.println("========================================");
                System.out.println("CHAT_MEMBER_NOT_LOGIN 발생");
                System.out.println("========================================");
            }
        }
    }

    @Test
    @DisplayName("채팅방 입장 테스트: 에러 - 채팅방이 없음")
    void createChatMemberNoChatRoomTest() {
        chatRoomRepository.deleteAll();

        try {
            chatMemberService.createChatMember(chatRoomId, userDetails1);
        } catch (ErrorException e) {
            if (e.getErrorCode() == CHAT_ROOM_NOT_FOUND) {
                System.out.println("========================================");
                System.out.println("CHAT_ROOM_NOT_FOUND 발생");
                System.out.println("========================================");
            }
        }
    }

    @Test
    @DisplayName("채팅방 입장 테스트: 에러 - 이미 참여중")
    void createChatMemberAlreadyJoinTest() {
        chatMemberService.createChatMember(chatRoomId, userDetails1);

        try {
            chatMemberService.createChatMember(chatRoomId, userDetails1);
        } catch (ErrorException e) {
            if (e.getErrorCode() == CHAT_MEMBER_ALREADY_EXISTS) {
                System.out.println("========================================");
                System.out.println("CHAT_MEMBER_ALREADY_EXISTS 발생");
                System.out.println("========================================");
            }
        }
    }

    @Test
    @DisplayName("멤버 상태 변경 테스트: 성공")
    void updateChatMemberTest() {
        chatMemberService.createChatMember(chatRoomId, userDetails1);

        chatMemberService.updateChatMember(chatRoomId, userDetails1, Status.LEFT);
        chatMemberService.updateChatMember(chatRoomId, userDetails1, Status.ONLINE);
        chatMemberService.updateChatMember(chatRoomId, userDetails1, Status.OFFLINE);
    }

    @Test
    @DisplayName("멤버 상태 변경 테스트: 에러 - 비로그인")
    void updateChatMemberNotLoginTest() {
        try {
            chatMemberService.updateChatMember(chatRoomId, null, Status.ONLINE);
        }
        catch (ErrorException e) {
            if (e.getErrorCode() == CHAT_MEMBER_NOT_LOGIN) {
                System.out.println("========================================");
                System.out.println("CHAT_MEMBER_NOT_LOGIN 발생");
                System.out.println("========================================");
            }
        }
    }

    @Test
    @DisplayName("멤버 상태 변경 테스트: 에러 - 채팅방이 없음")
    void updateChatMemberNotFoundChatRoomTest() {
        try {
            chatMemberService.updateChatMember(chatRoomId, null, Status.ONLINE);
        }
        catch (ErrorException e) {
            if (e.getErrorCode() == CHAT_ROOM_NOT_FOUND) {
                System.out.println("========================================");
                System.out.println("CHAT_ROOM_NOT_FOUND 발생");
                System.out.println("========================================");

            }
        }
    }

    @Test
    @DisplayName("멤버 상태 변경 테스트: 에러 - 이미 참여중")
    void updateChatMemberJoinChatRoomTest() {
        try {
            chatMemberService.updateChatMember(chatRoomId, null, Status.ONLINE);
        }
        catch (ErrorException e) {
            if (e.getErrorCode() == CHAT_MEMBER_ALREADY_EXISTS) {
                System.out.println("========================================");
                System.out.println("CHAT_MEMBER_ALREADY_EXISTS 발생");
                System.out.println("========================================");

            }
        }
    }
}