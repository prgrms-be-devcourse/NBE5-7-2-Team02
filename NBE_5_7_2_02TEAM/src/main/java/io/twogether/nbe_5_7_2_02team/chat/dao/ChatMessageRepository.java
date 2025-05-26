package io.twogether.nbe_5_7_2_02team.chat.dao;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMessage;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatroom);

    ChatMessage findByIdAndChatRoomAndChatMember(Long id, ChatRoom chatRoom, ChatMember chatMember);

    void deleteByChatRoom(ChatRoom chatRoom);
}
