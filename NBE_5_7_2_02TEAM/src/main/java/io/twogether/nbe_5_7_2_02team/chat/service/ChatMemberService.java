package io.twogether.nbe_5_7_2_02team.chat.service;

import static io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus.ONLINE;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_ALREADY_EXISTS;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_NOT_ENTER;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_UNDEFINED_STATUS;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_EMPTY;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMemberRepository;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.chat.dto.ChatMemberGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.util.CheckUserLogin;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMemberService {

    private final ChatRoomService chatRoomService;

    private final ChatMemberRepository chatMemberRepository;
    private final CheckUserLogin checkUserLogin;

    @Transactional(readOnly = true)
    public List<ChatMemberGetResponse> getChatMember(Long chatroomId) {
        ChatRoom chatRoom = chatRoomService.checkChatRoomExists(chatroomId);

        List<ChatMember> chatMemberList = chatMemberRepository.findByChatRoom(chatRoom);

        if (chatMemberList.isEmpty()) {
            throw new ErrorException(CHAT_ROOM_EMPTY);
        }

        return chatMemberList.stream().map(ChatMemberGetResponse::from).toList();
    }

    @Transactional
    public Long createChatMember(Long chatroomId, UserDetails userDetails) {
        Member member = checkUserLogin.checkUserLogin(userDetails);

        ChatRoom chatRoom = chatRoomService.checkChatRoomExists(chatroomId);

        ChatMember chatMember = chatMemberRepository.findByChatRoomAndMember(chatRoom, member);

        if (chatMember != null) {
            throw new ErrorException(CHAT_MEMBER_ALREADY_EXISTS);
        }

        return chatMemberRepository
                .save(
                        ChatMember.builder()
                                .chatRoom(chatRoom)
                                .member(member)
                                .chatMemberStatus(ONLINE)
                                .build())
                .getId();
    }

    @Transactional
    public Long updateChatMember(
            Long chatroomId, UserDetails userDetails, ChatMemberStatus chatMemberStatus) {
        Member member = checkUserLogin.checkUserLogin(userDetails);

        ChatRoom chatRoom = chatRoomService.checkChatRoomExists(chatroomId);

        ChatMember chatMember = chatMemberRepository.findByChatRoomAndMember(chatRoom, member);

        if (chatMember == null) {
            throw new ErrorException(CHAT_MEMBER_NOT_ENTER);
        }

        if (chatMemberStatus == null) {
            throw new ErrorException(CHAT_MEMBER_UNDEFINED_STATUS);
        }

        chatMember.updateStatus(chatMemberStatus);
        return chatMemberRepository.save(chatMember).getId();
    }
}
