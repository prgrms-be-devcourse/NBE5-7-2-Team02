package io.twogether.nbe_5_7_2_02team.tag.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.twogether.nbe_5_7_2_02team.global.annotation.FlywayReset;
import io.twogether.nbe_5_7_2_02team.tag.dao.TagRepository;
import io.twogether.nbe_5_7_2_02team.tag.domain.Tag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@FlywayReset
@SpringBootTest
@Transactional
class TagServiceTest {

    @Autowired private TagService tagService;
    @Autowired private TagRepository tagRepository;

    private List<Tag> tags;

    @BeforeEach
    void setUp() {
        tags = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < random.nextInt(10) + 1; i++) {
            Tag tag = Tag.builder().name("TAG-" + i).build();
            tags.add(tag);
            tagRepository.save(tag);
        }
    }

    @Test
    @DisplayName("모든 태그 조회")
    void findAllTags() throws Exception {
        // given & when
        List<Tag> findTags = tagService.getAllTags();

        // then
        assertThat(findTags).isEqualTo(tags);
    }
}
