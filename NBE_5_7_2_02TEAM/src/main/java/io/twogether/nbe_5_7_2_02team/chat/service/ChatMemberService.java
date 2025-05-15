package io.twogether.nbe_5_7_2_02team.chat.service;

import static io.twogether.nbe_5_7_2_02team.chat.domain.Status.ONLINE;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_ALREADY_EXISTS;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_EMPTY;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMemberRepository;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.chat.domain.Status;
import io.twogether.nbe_5_7_2_02team.chat.dto.ChatMemberResponse;
import io.twogether.nbe_5_7_2_02team.chat.util.CheckUserLogin;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMemberService {

    private final ChatRoomService chatRoomService;

    private final ChatMemberRepository chatMemberRepository;
    private final CheckUserLogin checkUserLogin;

    @Transactional
    public List<ChatMemberResponse> getChatMember(Long chatroomId) {
        ChatRoom chatRoom = chatRoomService.checkChatRoomExists(chatroomId);

        List<ChatMember> chatMemberList = chatMemberRepository.findByChatRoom(chatRoom);

        if (chatMemberList.isEmpty()) {
            throw new ErrorException(CHAT_ROOM_EMPTY);
        }

        return chatMemberList.stream()
            .map(ChatMemberResponse::from)
            .toList();
    }

    @Transactional
    public Long createChatMember(Long chatroomId, UserDetails userDetails) {
        Member member = checkUserLogin.checkUserLogin(userDetails);

        ChatRoom chatRoom = chatRoomService.checkChatRoomExists(chatroomId);

        ChatMember chatMember = chatMemberRepository.findByChatRoomAndMember(chatRoom, member);

        if (chatMember != null) {
            throw new ErrorException(CHAT_MEMBER_ALREADY_EXISTS);

        }

        return chatMemberRepository.save(
            ChatMember
                .builder()
                .chatRoom(chatRoom)
                .member(member)
                .status(ONLINE)
                .build()
        ).getId();
    }

    @Transactional
    public Long updateChatMember(Long chatroomId, UserDetails userDetails, Status status) {
        Member member = checkUserLogin.checkUserLogin(userDetails);

        ChatRoom chatRoom = chatRoomService.checkChatRoomExists(chatroomId);

        ChatMember chatMember = chatMemberRepository.findByChatRoomAndMember(chatRoom, member);

        if (chatMember != null) {
            throw new ErrorException(CHAT_MEMBER_ALREADY_EXISTS);

        }

        chatMember.updateStatus(status);

        return chatMemberRepository.save(chatMember).getId();
    }
}
