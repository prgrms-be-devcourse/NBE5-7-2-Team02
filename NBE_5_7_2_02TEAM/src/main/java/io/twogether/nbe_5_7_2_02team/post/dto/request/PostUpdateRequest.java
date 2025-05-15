package io.twogether.nbe_5_7_2_02team.post.dto.request;

import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PostUpdateRequest {

    private String title;
    private String content;
    private RecruitmentStatus recruitmentStatus;
    private List<MultipartFile> images;
    private List<String> tags;
}
