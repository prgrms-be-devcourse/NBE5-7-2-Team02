package io.twogether.nbe_5_7_2_02team.chat.dao;

import io.twogether.nbe_5_7_2_02team.post.domain.Post;

public interface ChatRepository {

    void deleteByPost(Post post);
}
