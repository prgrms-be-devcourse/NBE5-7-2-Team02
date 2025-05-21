package io.twogether.nbe_5_7_2_02team.chat.dao;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    ChatMember findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    List<ChatMember> findByChatRoom(ChatRoom chatRoom);

    Optional<ChatMember> findByMember(Member member);
}
