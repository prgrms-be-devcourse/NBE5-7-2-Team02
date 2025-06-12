package io.twogether.nbe_5_7_2_02team.post.dto.request;

import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostUpdateRequest {

    private String title;
    private String content;
    private RecruitmentStatus recruitmentStatus;
    private LocalDate recruitmentDeadline;
    private List<MultipartFile> images;
    private List<String> tags;
    private List<RecruitmentFieldRequest> recruitmentFields = new ArrayList<>();
}
