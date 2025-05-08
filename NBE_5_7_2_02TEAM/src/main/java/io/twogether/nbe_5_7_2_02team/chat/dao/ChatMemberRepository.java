package io.twogether.nbe_5_7_2_02team.chat.dao;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

}
