package io.twogether.nbe_5_7_2_02team.browser;

import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.DONE;
import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.NONE;
import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.RECRUITING;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.twogether.nbe_5_7_2_02team.browser.template.BrowserTestTemplate;
import io.twogether.nbe_5_7_2_02team.member.dao.FollowRepository;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Follow;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
public class PostBrowserSuccessTest extends BrowserTestTemplate {

    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired FollowRepository followRepository;

    Random random = new Random();

    @AllArgsConstructor
    static class PostCreateRequest {
        private String title;
        private String content;
        private List<String> tags;
        private RecruitmentStatus recruitmentStatus;
    }

    @BeforeEach
    public void setup() {
        // Dummy Post 생성
        for (int i = 0; i < random.nextInt(90) + 10; i++) {
            postRepository.save(
                    Post.builder()
                            .title("TITLE-" + i)
                            .content("CONTENT-" + i)
                            .member(member)
                            .recruitmentStatus(NONE)
                            .build());
        }
    }

    @ParameterizedTest
    @MethodSource("postRequestProvider")
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
        // when & then
        mockMvc.perform(
                        multipart("/api/posts/" + member.getId())
                                .param("title", request.title)
                                .param("content", request.content)
                                .param("tags", request.tags.toArray(new String[0]))
                                .param("recruitmentStatus", request.recruitmentStatus.toString())
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET: /api/posts 비회원 접근 - 필터 없음")
    void getPosts() throws Exception {
        // when & then
        mockMvc.perform(get("/api/posts").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts.length()").value(10));
    }

    @Test
    @DisplayName("GET: /api/posts 비회원 접근 - 모집 여부 필터링")
    void getPostsWithRecruitStatus() throws Exception {
        // given
        PostCreateRequest request =
                new PostCreateRequest("TITLE", "CONTENT", List.of("TAG"), RECRUITING);
        createPostHelper(request);

        // when & then
        mockMvc.perform(get("/api/posts").param("limit", "10").param("isRecruit", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts.length()").value(1))
                .andExpect(
                        jsonPath("$.data.posts[0].recruitment_status")
                                .value(RECRUITING.toString()));
    }

    @Test
    @DisplayName("GET: /api/posts 비회원 접근 - 태그 필터링")
    void getPostsWithTag() throws Exception {
        // given
        String targetTag = "TAG";
        PostCreateRequest request =
                new PostCreateRequest("TITLE", "CONTENT", List.of(targetTag), RECRUITING);
        createPostHelper(request);

        // when & then
        mockMvc.perform(
                        get("/api/posts")
                                .param("limit", "10")
                                .param("tags", targetTag)
                                .param("isRecruit", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts.length()").value(1))
                .andExpect(jsonPath("$.data.posts[0].tags[0]").value(targetTag));
    }

    @Test
    @DisplayName("GET: /api/posts 비회원 접근 - 여러 개 태그 필터링")
    void getPostsWithTags() throws Exception {
        // given
        String[] targetTags = {"TEST_TAG_1", "TEST_TAG_2"};
        PostCreateRequest targetRequest =
                new PostCreateRequest(
                        "TARGET_TITLE", "TARGET_CONTENT", List.of(targetTags), RECRUITING);
        PostCreateRequest fakeRequest =
                new PostCreateRequest(
                        "FAKE_TITLE", "FAKE_CONTENT", List.of(targetTags[0]), RECRUITING);
        createPostHelper(targetRequest);
        createPostHelper(fakeRequest);

        // when & then
        mockMvc.perform(
                        get("/api/posts")
                                .param("limit", "10")
                                .param("tags", targetTags)
                                .param("isRecruit", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts.length()").value(1))
                .andExpect(jsonPath("$.data.posts[0].tags.length()").value(2))
                .andExpect(jsonPath("$.data.posts[0].tags[0]").value(targetTags[0]))
                .andExpect(jsonPath("$.data.posts[0].tags[1]").value(targetTags[1]));
    }

    @Test
    @DisplayName("GET: /api/posts 회원 접근 - 팔로잉 필터링")
    void getPostsWithFollowing() throws Exception {
        // given
        Member followingMember =
                Member.builder()
                        .name("FOLLOWING_MEMBER")
                        .email("FOLLOWING_MEMBER@example.com")
                        .build();
        memberRepository.save(followingMember);
        followRepository.save(new Follow(member, followingMember));

        for (int i = 0; i < 5; i++) {
            postRepository.save(
                    Post.builder()
                            .title("TITLE-" + i)
                            .content("CONTENT-" + i)
                            .member(followingMember)
                            .recruitmentStatus(NONE)
                            .build());
        }

        // when & then
        mockMvc.perform(
                        get("/api/posts")
                                .param("limit", "10")
                                .param("isFollowing", "true")
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts.length()").value(5))
                .andExpect(jsonPath("$.data.posts[0].member_id").value(followingMember.getId()));
    }
}
