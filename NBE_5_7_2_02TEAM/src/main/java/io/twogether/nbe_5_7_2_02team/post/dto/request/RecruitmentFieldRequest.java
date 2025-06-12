package io.twogether.nbe_5_7_2_02team.post.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecruitmentFieldRequest {

    @NotBlank private String fieldName;

    @Min(1)
    private int totalCount;
}
