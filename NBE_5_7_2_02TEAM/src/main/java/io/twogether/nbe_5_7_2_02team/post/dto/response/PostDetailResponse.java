package io.twogether.nbe_5_7_2_02team.post.dto.response;

import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetailResponse {
    private String title;
    private String content;
    private RecruitmentStatus recruitmentStatus;
    private List<String> tags;
    private List<String> imageUrls;
}
