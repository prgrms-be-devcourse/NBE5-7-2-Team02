package io.twogether.nbe_5_7_2_02team.tag.dao;

import io.twogether.nbe_5_7_2_02team.tag.domain.Tag;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    @Query(
        """
        DELETE FROM Tag t
                WHERE t.id NOT IN :referredTagIds
        """
    )
    @Modifying
    void deleteUnusedTags(List<Long> referredTagIds);
}
