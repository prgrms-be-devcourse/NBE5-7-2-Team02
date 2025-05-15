package io.twogether.nbe_5_7_2_02team.chat.service;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_ALREADY_EXISTS;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_LIST_EMPTY;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.POST_NOT_FOUND;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatRoomRepository;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.chat.dto.ChatRoomResponse;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private PostRepository postRepository;
    Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder()
            .title("제목")
            .content("내용")
            .recruitmentStatus(RecruitmentStatus.NONE).build();

        postRepository.save(post);
    }

    @Test
    @DisplayName("채팅방 생성 테스트: 성공")
    void createChatRoomTest() {
        Long id = chatRoomService.createChatroom(post.getId());

        Optional<ChatRoom> byId = chatRoomRepository.findById(id);

        ChatRoom chatRoom = byId.get();

        System.out.println("========================================");
        System.out.println("POST: " + post.getId());
        System.out.println("========================================");
        System.out.println("ChatRoom ID: " + chatRoom.getId());
        System.out.println("ChatRoom POST: " + chatRoom.getPost().getId());
        System.out.println("createdAt: " + chatRoom.getCreatedAt());
        System.out.println("updatedAt: " + chatRoom.getUpdatedAt());
        System.out.println("========================================");
    }

    @Test
    @DisplayName("채팅방 생성 테스트: 에러 - 없는 게시글 생성 테스트")
    void createChatRoomNotFoundPostTest() {
        try {
            chatRoomService.createChatroom(1000L);
        } catch (ErrorException e) {
            if (e.getErrorCode() == POST_NOT_FOUND) {
                System.out.println("========================================");
                System.out.println("POST_NOT_FOUND 발생");
                System.out.println("========================================");
            }
        }
    }

    @Test
    @DisplayName("채팅방 생성 테스트: 에러 - 중복생성 테스트")
    void createChatRoomDuplicateTest() {
        chatRoomService.createChatroom(post.getId());

        try {
            chatRoomService.createChatroom(post.getId());
        } catch (ErrorException e) {
            if (e.getErrorCode() == CHAT_ROOM_ALREADY_EXISTS) {
                System.out.println("========================================");
                System.out.println("CHAT_ROOM_ALREADY_EXISTS 발생");
                System.out.println("========================================");
            }
        }
    }

    @Test
    @DisplayName("채팅방 목록 조회 테스트: 성공")
    void getChatRoomListTest() {
        chatRoomService.createChatroom(post.getId());

        List<ChatRoomResponse> chatRoomList = chatRoomService.getChatRoomList();

        System.out.println("========================================");
        for (ChatRoomResponse chatRoom : chatRoomList) {
            System.out.println("ID: " + chatRoom.getId());
        }
        System.out.println("========================================");
    }

    @Test
    @DisplayName("채팅방 목록 조회 테스트: 에러 - 채팅방이 존재하지 않음")
    void getChatRoomListEmptyTest() {
        chatRoomRepository.deleteAll();

        try {
            chatRoomService.getChatRoomList();
        } catch (ErrorException e) {
            if (e.getErrorCode() == CHAT_ROOM_LIST_EMPTY) {
                System.out.println("========================================");
                System.out.println("CHAT_ROOM_LIST_EMPTY 발생");
                System.out.println("========================================");
            }
        }
    }

    @Test
    @DisplayName("채팅방 삭제")
    void deleteChatRoomTest() {
    }
}