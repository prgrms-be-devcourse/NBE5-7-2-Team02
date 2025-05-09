package io.twogether.nbe_5_7_2_02team.post.dao;

import io.twogether.nbe_5_7_2_02team.post.domain.Post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {}
