package io.twogether.nbe_5_7_2_02team.tag.dto.response;

import io.twogether.nbe_5_7_2_02team.tag.domain.Tag;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
