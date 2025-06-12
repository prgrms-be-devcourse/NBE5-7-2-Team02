package io.twogether.nbe_5_7_2_02team.tag.api;

import io.twogether.nbe_5_7_2_02team.tag.dto.response.TagGetResponse;
import io.twogether.nbe_5_7_2_02team.tag.service.TagService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<TagGetResponse> getTag() {
        TagGetResponse tagGetResponse = TagGetResponse.of(tagService.getAllTags());

        return tagGetResponse.getTags().isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tagGetResponse);
    }
}
