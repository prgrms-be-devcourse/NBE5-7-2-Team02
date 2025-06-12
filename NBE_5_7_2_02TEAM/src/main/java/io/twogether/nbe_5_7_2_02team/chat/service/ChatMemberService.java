package io.twogether.nbe_5_7_2_02team.chat.service;

import static io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus.LEFT;
import static io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus.OFFLINE;
import static io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus.ONLINE;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_NOT_ENTER;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_UNDEFINED_STATUS;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_EMPTY;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMemberRepository;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatMemberGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatRoomGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.util.CheckUserLogin;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMemberService {

    private final ChatRoomService chatRoomService;

    private final ChatMemberRepository chatMemberRepository;
    private final CheckUserLogin checkUserLogin;

    @Transactional(readOnly = true)
    public List<ChatRoomGetResponse> getChatRoomListByUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = checkUserLogin.checkUserLogin(userDetails);

        List<ChatMember> chatMemberList =
                chatMemberRepository.findByMemberAndChatMemberStatusIn(
                        member, Arrays.asList(ONLINE, OFFLINE));

        return chatMemberList.stream()
                .map(chatMember -> ChatRoomGetResponse.from(chatMember.getChatRoom()))
                .toList();
    }

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
            if (chatMember.getChatMemberStatus() == LEFT) {
                chatMember.setChatMemberStatus(ONLINE);
            }

            return chatMember.getId();
        }

        Long id =
                chatMemberRepository
                        .save(
                                ChatMember.builder()
                                        .chatRoom(chatRoom)
                                        .member(member)
                                        .chatMemberStatus(ONLINE)
                                        .build())
                        .getId();

        long size = chatMemberRepository.countByChatRoom(chatRoom);

        chatRoom.setMemberCount(size);

        return id;
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

        chatMember.setChatMemberStatus(chatMemberStatus);
        return chatMemberRepository.save(chatMember).getId();
    }
}
