package io.twogether.nbe_5_7_2_02team.browser;

import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.DONE;
import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.NONE;
import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.RECRUITING;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.database.rider.core.api.dataset.DataSet;

import io.twogether.nbe_5_7_2_02team.browser.template.BrowserTestTemplate;
import io.twogether.nbe_5_7_2_02team.global.annotation.FlywayReset;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostUpdateRequest;
import io.twogether.nbe_5_7_2_02team.tag.dao.TagRepository;

import lombok.AllArgsConstructor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@FlywayReset
public class PostBrowserSuccessTest extends BrowserTestTemplate {

    @Autowired MemberRepository memberRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private PostRepository postRepository;

    @AllArgsConstructor
    static class PostCreateRequest {
        private String title;
        private String content;
        private List<String> tags;
        private RecruitmentStatus recruitmentStatus;
    }

    @ParameterizedTest
    @MethodSource("postRequestProvider")
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    @DisplayName("POST: /api/posts 게시글 생성")
    void createPost(PostCreateRequest request) throws Exception {
        createPostHelper(request);
    }

    static Stream<PostCreateRequest> postRequestProvider() {
        return Stream.of(
                new PostCreateRequest("TITLE", "CONTENT", List.of("TAG"), NONE),
                new PostCreateRequest("TITLE", "CONTENT", List.of("TAG"), DONE),
                new PostCreateRequest("TITLE", "CONTENT", List.of("TAG", "TAG-2"), RECRUITING));
    }

    void createPostHelper(PostCreateRequest request) throws Exception {
        // given
        TokenPair tokenPair = getTokenPair(1L);

        // when & then
        mockMvc.perform(
                        multipart("/api/posts")
                                .param("title", request.title)
                                .param("content", request.content)
                                .param("tags", request.tags.toArray(new String[0]))
                                .param("recruitmentStatus", request.recruitmentStatus.toString())
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isCreated());
    }

    @Test
    @DataSet(
            value = {"datasets/v2/member.yml, datasets/v2/post.yml"},
            cleanBefore = true,
            cleanAfter = true)
    @DisplayName("GET: /api/posts 비회원 접근 - 필터 없음")
    void getPosts() throws Exception {
        // when & then
        mockMvc.perform(get("/api/posts").param("limit", "10"))
                .andExpectAll(status().isOk(), jsonPath("$.posts.length()").value(4));
    }

    @Test
    @DataSet(
            value = {"datasets/v2/member.yml, datasets/v2/post.yml"},
            cleanBefore = true,
            cleanAfter = true)
    @DisplayName("GET: /api/posts 비회원 접근 - 모집 여부 필터링")
    void getPostsWithRecruitStatus() throws Exception {
        // when & then
        mockMvc.perform(get("/api/posts").param("limit", "10").param("isRecruit", "true"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.posts.length()").value(2),
                        jsonPath("$.posts[0].recruitment_status").value(RECRUITING.toString()));
    }

    @Test
    @DataSet(
            value = {
                "datasets/v2/member.yml",
                "datasets/v2/post.yml",
                "datasets/v2/tag.yml",
            },
            cleanBefore = true,
            cleanAfter = true)
    @DisplayName("GET: /api/posts 비회원 접근 - 태그 필터링")
    void getPostsWithTag() throws Exception {
        // given
        String targetTag = "TAG-1";

        // when & then
        mockMvc.perform(get("/api/posts").param("limit", "10").param("tags", targetTag))
                .andExpectAll(status().isOk(), jsonPath("$.posts.length()").value(2));
    }

    @Test
    @DataSet(
            value = {
                "datasets/v2/member.yml",
                "datasets/v2/post.yml",
                "datasets/v2/tag.yml",
            },
            cleanBefore = true,
            cleanAfter = true)
    @DisplayName("GET: /api/posts 비회원 접근 - 여러 개 태그 필터링")
    void getPostsWithTags() throws Exception {
        // given
        String[] targetTags = {"TAG-1", "TAG-2"};

        // when & then
        mockMvc.perform(get("/api/posts").param("limit", "10").param("tags", targetTags))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.posts.length()").value(1),
                        jsonPath("$.posts[0].tags.length()").value(2),
                        jsonPath("$.posts[0].tags").value(containsInAnyOrder(targetTags)));
    }

    @Test
    @DataSet(
            value = {"datasets/v2/member.yml", "datasets/v2/post.yml", "datasets/v2/follow.yml"},
            cleanBefore = true,
            cleanAfter = true)
    @DisplayName("GET: /api/posts 회원 접근 - 팔로잉 필터링")
    void getPostsWithFollowing() throws Exception {
        // given
        // member1이 member2를 팔로우
        long followerId = 1L;
        long targetMemberId = 2L;
        TokenPair tokenPair = getTokenPair(followerId);

        // when & then
        mockMvc.perform(
                        get("/api/posts")
                                .param("limit", "10")
                                .param("isFollowing", "true")
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.posts.length()").value(1),
                        jsonPath("$.posts[0].member_id").value(targetMemberId));
    }

    @Test
    @DataSet(
            value = {"datasets/v2/member.yml, datasets/v2/post.yml"},
            cleanBefore = true,
            cleanAfter = true)
    @DisplayName("GET: /api/posts/member/{memberId} 회원 접근 - 특정 멤버 작성 게시글 조회")
    void getPostsWithMemberId() throws Exception {
        // given
        long targetMemberId = 1;
        TokenPair tokenPair = getTokenPair(targetMemberId);

        // when & then
        mockMvc.perform(
                        get("/api/posts/member/" + targetMemberId)
                                .param("limit", "10")
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.posts.length()").value(3),
                        jsonPath("$.posts[0].member_id").value(targetMemberId));
    }

    @Test
    @DataSet(
            value = {"datasets/v2/member.yml, datasets/v2/post.yml"},
            cleanBefore = true,
            cleanAfter = true)
    @DisplayName("GET: /api/posts/member/{memberId} 회원 접근 - 페이징 테스트")
    void getPostsPaging() throws Exception {
        // given
        long targetMemberId = 1;
        TokenPair tokenPair = getTokenPair(targetMemberId);

        // when & then
        /*
         * [최신 Post 조회 검증]
         * posts 리스트는 index 순으로 오래된 항목부터 최신 항목까지 저장되어 있음.
         * 따라서 "limit=1"을 통해 조회할 경우, 가장 최신 post 반환해야 함.
         */
        mockMvc.perform(
                        get("/api/posts/member/" + targetMemberId)
                                .param("limit", "1")
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.posts.length()").value(1),
                        jsonPath("$.posts[0].member_id").value(targetMemberId),
                        jsonPath("$.posts[0].post_id").value(3));

        /*
         * [Offset 조회 검증]
         * "lastPostId"에 해당하는 Post 이후로 가장 최신의 post를 조회.
         */
        mockMvc.perform(
                        get("/api/posts/member/" + targetMemberId)
                                .param("limit", "1")
                                .param("lastPostId", "2")
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.posts.length()").value(1),
                        jsonPath("$.posts[0].member_id").value(targetMemberId),
                        jsonPath("$.posts[0].post_id").value(1));
    }

    @Test
    @DataSet(
            value = {
                "datasets/v2/member.yml",
                "datasets/v2/post.yml",
                "datasets/v2/tag.yml",
            },
            cleanBefore = true,
            cleanAfter = true)
    @DisplayName("DELETE: /api/posts/{postId} 회원 접근 - 게시글 삭제 시 참조를 잃은 태그 또한 삭제")
    void deletePostWithCheckingUnusedTags() throws Exception {
        // given
        long targetMemberId = 1L;
        long targetPostId = 2L;
        TokenPair tokenPair = getTokenPair(targetMemberId);

        // when
        mockMvc.perform(
                        delete("/api/posts/" + targetPostId)
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isOk());

        // then : 2번 게시글 삭제 시 2번 태그는 참조를 잃어 삭제되어야 함
        assertThat(postRepository.findById(targetPostId).isPresent()).isFalse();
        assertThat(tagRepository.findById(2L).isPresent()).isFalse();
    }

    @Test
    @DataSet(
            value = {
                "datasets/v2/member.yml",
                "datasets/v2/post.yml",
                "datasets/v2/tag.yml",
            },
            cleanBefore = true,
            cleanAfter = true)
    @DisplayName("PATCH: /api/posts/{postId} 회원 접근 - 게시글 수정 시 참조를 잃은 태그 또한 삭제")
    void patchPostWithCheckingUnusedTags() throws Exception {
        // given
        long targetMemberId = 1L;
        long targetPostId = 2L;
        TokenPair tokenPair = getTokenPair(targetMemberId);

        PostUpdateRequest request = new PostUpdateRequest();
        request.setTitle("NEW TITLE");
        request.setContent("NEW CONTENT");
        request.setRecruitmentStatus(DONE);

        // when
        mockMvc.perform(
                        patch("/api/posts/" + targetPostId)
                                .param("title", request.getTitle())
                                .param("content", request.getContent())
                                .param("recruitmentStatus", request.getRecruitmentStatus().name())
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpectAll(status().isOk(), jsonPath("$.id").value(targetPostId));

        // then : 2번 게시글의 태그를 모두 삭제하는 것으로 2번 태그는 참조를 잃어 삭제되어야 함
        Optional<Post> modifiedPostOptional = postRepository.findById(targetPostId);
        assertThat(modifiedPostOptional.isPresent()).isTrue();

        Post modifiedPost = modifiedPostOptional.get();
        assertThat(modifiedPost.getTitle()).isEqualTo(request.getTitle());
        assertThat(modifiedPost.getContent()).isEqualTo(request.getContent());
        assertThat(modifiedPost.getRecruitmentStatus()).isEqualTo(request.getRecruitmentStatus());

        assertThat(tagRepository.findById(2L).isPresent()).isFalse();
    }

    private TokenPair getTokenPair(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        return jwtTokenProvider.generateTokenPair(member);
    }
}
