package io.twogether.nbe_5_7_2_02team.chat.dao;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    ChatMember findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    List<ChatMember> findByChatRoom(ChatRoom chatRoom);

    List<ChatMember> findByMemberAndChatMemberStatusIn(
            Member member, Collection<ChatMemberStatus> chatMemberStatuses);

    void deleteByChatRoom(ChatRoom chatRoom);

    long countByChatRoom(ChatRoom chatRoom);
}
