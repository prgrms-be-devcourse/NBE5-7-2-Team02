package io.twogether.nbe_5_7_2_02team.db;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import io.twogether.nbe_5_7_2_02team.db.template.MigrationTestTemplate;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import jakarta.annotation.PostConstruct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@DBRider
class V2MigrationTest extends MigrationTestTemplate {

    @PostConstruct
    void setUp() {
        cleanAndMigrate("1");
    }

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Test
    @DataSet(value = {
        "datasets/v1/member.yml",
        "datasets/v1/post.yml"
    }, cleanAfter = true)
    @DisplayName("V1에서 V2로 마이그레이션")
    void migrateV1toV2() throws Exception {
        // given
        migrate("2");

        // when & then
        assertDoesNotThrow(this::checkMember);
        assertDoesNotThrow(this::checkPost);
    }

    void checkMember() throws Exception {
        // given
        Member member = Member.builder()
            .email("check@test.com")
            .githubId("check")
            .build();

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember).isEqualTo(member);
        assertThat(savedMember.getId()).isEqualTo(3);
        assertThat(memberRepository.count()).isEqualTo(3);
        assertThat(memberRepository.findById(1L)).isPresent();
        assertThat(memberRepository.findById(1L).get().getName()).isEqualTo("backend");
    }

    void checkPost() throws Exception {
        // given
        Member member = memberRepository.findById(1L).get();
        Post post = Post.builder()
            .title("checkPost-title")
            .content("checkPost-content")
            .member(member)
            .build();

        // when
        Post savedPost = postRepository.save(post);

        // then
        assertThat(savedPost).isEqualTo(post);
        assertThat(savedPost.getId()).isEqualTo(3);
        assertThat(postRepository.count()).isEqualTo(3);
        assertThat(postRepository.findById(1L)).isPresent();
        assertThat(postRepository.findById(1L).get().getTitle()).isEqualTo("Looking for Backend Dev");
        assertThatThrownBy(() -> memberRepository.delete(member))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}
