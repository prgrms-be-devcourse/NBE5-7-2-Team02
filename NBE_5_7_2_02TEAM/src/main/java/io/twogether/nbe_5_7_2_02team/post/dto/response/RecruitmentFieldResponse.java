package io.twogether.nbe_5_7_2_02team.post.dto.response;

import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentFieldResponse {
    private String fieldName;
    private int totalCount;
    private int currentCount;
    private boolean closed;

    public static RecruitmentFieldResponse from(RecruitmentField field) {
        return RecruitmentFieldResponse.builder()
                .fieldName(field.getFieldName())
                .totalCount(field.getTotalCount())
                .currentCount(field.getCurrentCount())
                .closed(field.isClosed())
                .build();
    }
}
