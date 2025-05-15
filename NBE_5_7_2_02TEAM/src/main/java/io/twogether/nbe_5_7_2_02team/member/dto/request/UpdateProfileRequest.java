package io.twogether.nbe_5_7_2_02team.member.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class UpdateProfileRequest {

    private String profileImage;

    public UpdateProfileRequest(String profileImage) {
        this.profileImage = profileImage;
    }

}
