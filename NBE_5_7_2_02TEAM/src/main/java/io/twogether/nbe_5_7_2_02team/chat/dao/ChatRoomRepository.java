package io.twogether.nbe_5_7_2_02team.chat.dao;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByPost(Post post);
}
