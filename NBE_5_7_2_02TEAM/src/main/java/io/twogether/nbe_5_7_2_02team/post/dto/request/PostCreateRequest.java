package io.twogether.nbe_5_7_2_02team.post.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
public class PostCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "모집 상태는 필수입니다.")
    private RecruitmentStatus recruitmentStatus;

    @FutureOrPresent(message = "마감일은 오늘 이후의 날짜여야 합니다.")
    private LocalDate recruitmentDeadline;

    @Size(max = 10, message = "이미지는 최대 10개까지만 업로드할 수 있습니다.")
    private List<MultipartFile> images;

    private List<String> tags = new ArrayList<>();

    private String recruitmentFieldsJson;

    @JsonIgnore private List<RecruitmentFieldRequest> recruitmentFields = new ArrayList<>();
}
