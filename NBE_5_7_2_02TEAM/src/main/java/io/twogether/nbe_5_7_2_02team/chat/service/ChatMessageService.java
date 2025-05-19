package io.twogether.nbe_5_7_2_02team.chat.service;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MEMBER_NOT_ENTER;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MESSAGE_CONTENT_BLANK;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_MESSAGE_NOT_FOUND;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMemberRepository;
import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMessageRepository;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMessage;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatMessageGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.dto.request.ChatMessagePostRequest;
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
public class ChatMessageService {

    private final ChatRoomService chatRoomService;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final CheckUserLogin checkUserLogin;

    @Transactional(readOnly = true)
    public List<ChatMessageGetResponse> getChatMessage(Long chatRoomId) {

        ChatRoom chatRoom = chatRoomService.checkChatRoomExists(chatRoomId);

        List<ChatMessage> chatMessageList =
                chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);

        return chatMessageList.stream().map(ChatMessageGetResponse::from).toList();
    }

    @Transactional
    public Long createChatMessage(
            Long chatRoomId,
            ChatMessagePostRequest chatMessagePostRequest,
            UserDetails userDetails) {

        Member member = checkUserLogin.checkUserLogin(userDetails);

        ChatRoom chatRoom = chatRoomService.checkChatRoomExists(chatRoomId);

        ChatMember chatMember = chatMemberRepository.findByChatRoomAndMember(chatRoom, member);

        if (chatMember == null) {
            throw new ErrorException(CHAT_MEMBER_NOT_ENTER);
        }

        String content = chatMessagePostRequest.getContent();

        if (content.isBlank()) {
            throw new ErrorException(CHAT_MESSAGE_CONTENT_BLANK);
        }

        return chatMessageRepository
                .save(
                        ChatMessage.builder()
                                .chatRoom(chatRoom)
                                .chatMember(chatMember)
                                .content(content)
                                .build())
                .getId();
    }

    @Transactional
    public void deleteChatMessage(Long chatMessageId, Long chatRoomId, UserDetails userDetails) {

        Member member = checkUserLogin.checkUserLogin(userDetails);

        ChatRoom chatRoom = chatRoomService.checkChatRoomExists(chatRoomId);

        ChatMember chatMember = chatMemberRepository.findByChatRoomAndMember(chatRoom, member);

        if (chatMember == null) {
            throw new ErrorException(CHAT_MEMBER_NOT_ENTER);
        }

        ChatMessage chatMessage =
                chatMessageRepository.findByIdAndChatRoomAndChatMember(
                        chatMessageId, chatRoom, chatMember);

        if (chatMessage == null) {
            throw new ErrorException(CHAT_MESSAGE_NOT_FOUND);
        }

        chatMessageRepository.deleteById(chatMessage.getId());
    }
}
