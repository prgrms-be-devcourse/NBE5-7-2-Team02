package io.twogether.nbe_5_7_2_02team.post.dao;

import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.PostTag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    void deleteAllByPost(Post post);
}
