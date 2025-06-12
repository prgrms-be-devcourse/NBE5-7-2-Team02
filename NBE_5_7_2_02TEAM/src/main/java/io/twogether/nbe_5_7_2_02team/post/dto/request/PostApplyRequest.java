package io.twogether.nbe_5_7_2_02team.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostApplyRequest {

    @NotBlank(message = "모집 분야는 필수입니다.")
    @JsonProperty("fieldName")
    private String fieldName;
}
