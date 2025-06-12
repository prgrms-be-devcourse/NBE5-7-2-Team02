package io.twogether.nbe_5_7_2_02team.post.util;

import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostBatchScheduler {

    private final PostRepository postRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateExpiredRecruitmentPosts() {
        LocalDate today = LocalDate.now();
        List<Post> expiredPosts =
                postRepository.findByRecruitmentStatusAndRecruitmentDeadlineBefore(
                        RecruitmentStatus.RECRUITING, today);

        if (expiredPosts.isEmpty()) {
            log.info("마감된 모집글 없음");
            return;
        }

        expiredPosts.forEach(post -> post.setRecruitmentStatus(RecruitmentStatus.DONE));
        log.info("{}개의 모집글이 DONE으로 변경됨", expiredPosts.size());
    }
}
