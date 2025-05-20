package io.twogether.nbe_5_7_2_02team.browser;

import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.DONE;
import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.NONE;
import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.RECRUITING;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.twogether.nbe_5_7_2_02team.browser.template.BrowserTestTemplate;
import io.twogether.nbe_5_7_2_02team.global.common.BaseEntity;
import io.twogether.nbe_5_7_2_02team.member.dao.FollowRepository;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Follow;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;

import lombok.AllArgsConstructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

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
        Member followingMember = createAndSaveMockMember();
        followRepository.save(new Follow(member, followingMember));
        createAndSaveMockPosts(followingMember, 5);

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

    @Test
    @DisplayName("GET: /api/posts/member/{memberId} 회원 접근 - 특정 멤버 작성 게시글 조회")
    void getPostsWithMemberId() throws Exception {
        // given
        Member targetMember = createAndSaveMockMember();
        createAndSaveMockPosts(targetMember, 5);

        // when & then
        mockMvc.perform(
                        get("/api/posts/member" + targetMember.getId())
                                .param("limit", "10")
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts.length()").value(5))
                .andExpect(jsonPath("$.data.posts[0].member_id").value(targetMember.getId()));
    }

    @Test
    @DisplayName("GET: /api/posts/member/{memberId} 회원 접근 - 페이징 테스트")
    void getPostsPaging() throws Exception {
        // given
        Member targetMember = createAndSaveMockMember();
        List<Post> posts = createAndSaveMockPosts(targetMember, 10);

        Field createdAtField = BaseEntity.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        LocalDateTime createdAt = LocalDateTime.now();
        for (Post post : posts) {
            createdAtField.set(post, createdAt);
            createdAt = createdAt.plusDays(1);
        }

        // when & then
        /*
         * [최신 Post 조회 검증]
         * posts 리스트는 index 순으로 오래된 항목부터 최신 항목까지 저장되어 있음.
         * 이에 따라 posts.getLast()는 가장 최신 항목을 반환함.
         * 따라서 "limit=1"을 통해 조회할 경우, 가장 최신 항목인 post.getLast()를 반환해야 함.
         */
        mockMvc.perform(
                        get("/api/posts/member" + targetMember.getId())
                                .param("limit", "1")
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts.length()").value(1))
                .andExpect(jsonPath("$.data.posts[0].member_id").value(targetMember.getId()))
                .andExpect(jsonPath("$.data.posts[0].post_id").value(posts.getLast().getId()));

        /*
         * [Offset 조회 검증]
         * "lastPostId"에 해당하는 Post 이후로 가장 최신의 post 항목들을 조회.
         * 아래 시험에서는 lastPostId가 posts.get(5)의 id를 나타내고 있음.
         * 따라서 "limit=1"을 통해 조회할 경우, posts.get(4)를 반환해야 함.
         */
        mockMvc.perform(
                        get("/api/posts/" + targetMember.getId())
                                .param("limit", "1")
                                .param("lastPostId", posts.get(5).getId().toString())
                                .header("Authorization", "Bearer " + tokenPair.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts.length()").value(1))
                .andExpect(jsonPath("$.data.posts[0].member_id").value(targetMember.getId()))
                .andExpect(jsonPath("$.data.posts[0].post_id").value(posts.get(4).getId()));
    }

    private List<Post> createAndSaveMockPosts(Member member, int numPosts) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < numPosts; i++) {
            Post post =
                    Post.builder()
                            .title("TARGET_TITLE-" + i)
                            .content("TARGET_CONTENT-" + i)
                            .member(member)
                            .recruitmentStatus(NONE)
                            .build();
            postRepository.save(post);
            posts.add(post);
        }
        return posts;
    }

    private Member createAndSaveMockMember() {
        Member targetMember =
                Member.builder()
                        .name("TARGET_MEMBER")
                        .email("TARGET_MEMBER@example.com")
                        .githubId("github.com")
                        .build();
        return memberRepository.save(targetMember);
    }
}
