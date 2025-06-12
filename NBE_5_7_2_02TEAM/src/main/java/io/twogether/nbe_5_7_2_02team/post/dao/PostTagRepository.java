package io.twogether.nbe_5_7_2_02team.post.dao;

import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.PostTag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    void deleteAllByPost(Post post);

    List<PostTag> findAllByPost(Post post);

    @Query("SELECT DISTINCT pt.tag.id FROM PostTag pt")
    List<Long> findAllTagIds();
}
