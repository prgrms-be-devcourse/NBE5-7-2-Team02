// package io.twogether.nbe_5_7_2_02team.chat.service;
//
// import static
// io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_NOT_ENTER;
// import static
// io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_NOT_LOGIN;
// import static
// io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MESSAGE_CONTENT_BLANK;
// import static
// io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MESSAGE_NOT_FOUND;
// import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_NOT_FOUND;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
//
// import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMemberRepository;
// import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMessageRepository;
// import io.twogether.nbe_5_7_2_02team.chat.dao.ChatRoomRepository;
// import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
// import io.twogether.nbe_5_7_2_02team.chat.dto.request.ChatMessagePostRequest;
// import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatMessageGetResponse;
// import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
// import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
// import io.twogether.nbe_5_7_2_02team.member.domain.Member;
// import io.twogether.nbe_5_7_2_02team.member.domain.Role;
// import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
// import io.twogether.nbe_5_7_2_02team.post.domain.Post;
// import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.test.context.support.WithMockUser;
//
// import java.util.Collections;
// import java.util.List;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @WithMockUser(username = "1", password = "<PASSWORD>")
// class ChatMessageServiceTest {
//
//    @Autowired private ChatRoomService chatRoomService;
//    @Autowired private ChatMemberService chatMemberService;
//    @Autowired private ChatMessageService chatMessageService;
//
//    @Autowired private ChatRoomRepository chatRoomRepository;
//    @Autowired private ChatMessageRepository chatMessageRepository;
//    @Autowired private PostRepository postRepository;
//    @Autowired private MemberRepository memberRepository;
//
//    UserDetails userDetails1;
//    UserDetails userDetails2;
//    Member member1;
//    Member member2;
//    Post post;
//
//    ChatMessagePostRequest chatMessagePostRequest;
//
//    ChatRoom chatRoom;
//    Long chatRoomId;
//
//    @Autowired private ChatMemberRepository chatMemberRepository;
//
//    @BeforeEach
//    void setUp() {
//        chatMessageRepository.deleteAll();
//        chatMemberRepository.deleteAll();
//        chatRoomRepository.deleteAll();
//        postRepository.deleteAll();
//        memberRepository.deleteAll();
//
//        member1 =
//                Member.builder()
//                        .email("test1@example.com")
//                        .name("testuser1")
//                        .githubId("123")
//                        .role(Role.MEMBER)
//                        .build();
//        member2 =
//                Member.builder()
//                        .email("test2@example.com")
//                        .name("testuser2")
//                        .githubId("456")
//                        .role(Role.MEMBER)
//                        .build();
//        memberRepository.save(member1);
//        memberRepository.save(member2);
//
//        userDetails1 =
//                User.builder()
//                        .username(String.valueOf(member1.getId()))
//                        .password("PASSWORD")
//                        .authorities(Collections.emptyList())
//                        .build();
//        userDetails2 =
//                User.builder()
//                        .username(String.valueOf(member2.getId()))
//                        .password("PASSWORD")
//                        .authorities(Collections.emptyList())
//                        .build();
//        post =
//                Post.builder()
//                        .title("제목")
//                        .content("내용")
//                        .recruitmentStatus(RecruitmentStatus.NONE)
//                        .build();
//        postRepository.save(post);
//
//        chatRoomId = chatRoomService.createChatroom(post.getId());
//        chatRoom = chatRoomService.checkChatRoomExists(chatRoomId);
//
//        chatMessagePostRequest = new ChatMessagePostRequest(member1.getId(), "메세지 내용");
//    }
//
//    @Test
//    @DisplayName("메세지 전송 테스트: 성공")
//    void createChatMessageTest() {
//        chatMemberService.createChatMember(chatRoomId, userDetails1);
//        chatMessageService.createChatMessage(chatRoomId, chatMessagePostRequest, userDetails1);
//    }
//
//    @Test
//    @DisplayName("메세지 전송 테스트: 에러 - 채팅방이 없음")
//    void createChatMessageNotFoundChatRoomTest() {
//        chatRoomRepository.deleteById(chatRoomId);
//
//        ErrorException errorException =
//                assertThrows(
//                        ErrorException.class,
//                        () ->
//                                chatMessageService.createChatMessage(
//                                        chatRoomId, chatMessagePostRequest, userDetails1));
//
//        assertEquals(CHAT_ROOM_NOT_FOUND, errorException.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("메세지 전송 테스트: 에러 - 비로그인")
//    void createChatMessageNotLoginTest() {
//        ErrorException errorException =
//                assertThrows(
//                        ErrorException.class,
//                        () ->
//                                chatMessageService.createChatMessage(
//                                        chatRoomId, chatMessagePostRequest, null));
//
//        assertEquals(CHAT_MEMBER_NOT_LOGIN, errorException.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("메세지 전송 테스트: 에러 - 채팅에 참여중이지 않음")
//    void createChatMessageNotEnterChatRoomTest() {
//        chatMemberRepository.deleteByMember(member1);
//
//        ErrorException errorException =
//                assertThrows(
//                        ErrorException.class,
//                        () ->
//                                chatMessageService.createChatMessage(
//                                        chatRoomId, chatMessagePostRequest, userDetails1));
//
//        assertEquals(CHAT_MEMBER_NOT_ENTER, errorException.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("메세지 전송 테스트: 에러 - 내용이 없음")
//    void createChatMessageMessageIsBlankTest() {
//        ChatMessagePostRequest emptyContent = new ChatMessagePostRequest(member1.getId(), "");
//        chatMemberService.createChatMember(chatRoomId, userDetails1);
//
//        ErrorException errorException =
//                assertThrows(
//                        ErrorException.class,
//                        () ->
//                                chatMessageService.createChatMessage(
//                                        chatRoomId, emptyContent, userDetails1));
//
//        assertEquals(CHAT_MESSAGE_CONTENT_BLANK, errorException.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("메세지 가져오기: 성공")
//    void getChatMessageTest() {
//        chatMemberService.createChatMember(chatRoomId, userDetails1);
//
//        chatMessageService.createChatMessage(chatRoomId, chatMessagePostRequest, userDetails1);
//        chatMessageService.createChatMessage(chatRoomId, chatMessagePostRequest, userDetails1);
//        chatMessageService.createChatMessage(chatRoomId, chatMessagePostRequest, userDetails1);
//        chatMessageService.createChatMessage(chatRoomId, chatMessagePostRequest, userDetails1);
//
//        List<ChatMessageGetResponse> chatMessageList =
//                chatMessageService.getChatMessage(chatRoomId);
//
//        System.out.println("========================================");
//        for (ChatMessageGetResponse chatMessageGetResponse : chatMessageList) {
//            System.out.println("chatMessageResponse.getId: " + chatMessageGetResponse.getId());
//            System.out.println(
//                    "chatMessageResponse.getChatMemberId: "
//                            + chatMessageGetResponse.getChatMemberId());
//            System.out.println(
//                    "chatMessageResponse.getContent: " + chatMessageGetResponse.getContent());
//            System.out.println(
//                    "chatMessageResponse.getCreatedAt: " + chatMessageGetResponse.getCreatedAt());
//        }
//        System.out.println("========================================");
//    }
//
//    @Test
//    @DisplayName("메세지 가져오기: 에러 - 없는 채팅방")
//    void getChatMessageNotFoundChatRoomTest() {
//        chatRoomRepository.deleteById(chatRoomId);
//
//        ErrorException errorException =
//                assertThrows(
//                        ErrorException.class, () ->
// chatMessageService.getChatMessage(chatRoomId));
//
//        assertEquals(CHAT_ROOM_NOT_FOUND, errorException.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("메세지 삭제: 성공")
//    void deleteChatMessageTest() {
//        chatMemberService.createChatMember(chatRoomId, userDetails1);
//        Long chatMessage =
//                chatMessageService.createChatMessage(
//                        chatRoomId, chatMessagePostRequest, userDetails1);
//
//        chatMessageService.deleteChatMessage(chatMessage, chatRoomId, userDetails1);
//    }
//
//    @Test
//    @DisplayName("메세지 삭제: 에러 - 채팅방 없음")
//    void deleteChatMessageNoChatRoomTest() {
//        chatMemberService.createChatMember(chatRoomId, userDetails1);
//        Long chatMessage =
//                chatMessageService.createChatMessage(
//                        chatRoomId, chatMessagePostRequest, userDetails1);
//
//        ErrorException errorException =
//                assertThrows(
//                        ErrorException.class,
//                        () ->
//                                chatMessageService.deleteChatMessage(
//                                        chatMessage, chatRoomId + 1L, userDetails1));
//
//        assertEquals(CHAT_ROOM_NOT_FOUND, errorException.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("메세지 삭제: 에러 - 참여중이지 않은 멤버")
//    void deleteChatMessageNotEnteredTest() {
//        chatMemberService.createChatMember(chatRoomId, userDetails1);
//        Long chatMessage =
//                chatMessageService.createChatMessage(
//                        chatRoomId, chatMessagePostRequest, userDetails1);
//
//        ErrorException errorException =
//                assertThrows(
//                        ErrorException.class,
//                        () ->
//                                chatMessageService.deleteChatMessage(
//                                        chatMessage, chatRoomId, userDetails2));
//
//        assertEquals(CHAT_MEMBER_NOT_ENTER, errorException.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("메세지 삭제: 에러 - 해당 메세지가 없음")
//    void deleteChatMessageNotFoundMessageTest() {
//        chatMemberService.createChatMember(chatRoomId, userDetails1);
//        Long chatMessage =
//                chatMessageService.createChatMessage(
//                        chatRoomId, chatMessagePostRequest, userDetails1);
//
//        ErrorException errorException =
//                assertThrows(
//                        ErrorException.class,
//                        () ->
//                                chatMessageService.deleteChatMessage(
//                                        chatMessage + 1L, chatRoomId, userDetails1));
//
//        assertEquals(CHAT_MESSAGE_NOT_FOUND, errorException.getErrorCode());
//    }
// }
