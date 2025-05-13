package io.twogether.nbe_5_7_2_02team.tag.dao;

import io.twogether.nbe_5_7_2_02team.tag.domain.Tag;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
}
