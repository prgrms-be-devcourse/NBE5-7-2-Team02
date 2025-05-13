package io.twogether.nbe_5_7_2_02team.tag.dto;

import io.twogether.nbe_5_7_2_02team.tag.domain.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class TagGetResponse {
    private List<String> tags = new ArrayList<>();

    private TagGetResponse(List<Tag> tags) {
        tags.forEach(tag -> this.tags.add(tag.getName()));
    }

    public static TagGetResponse of(List<Tag> tags) {
        return new TagGetResponse(tags);
    }
}
