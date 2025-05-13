package io.twogether.nbe_5_7_2_02team.browser;

import io.twogether.nbe_5_7_2_02team.browser.template.BrowserTestTemplate;
import io.twogether.nbe_5_7_2_02team.tag.dao.TagRepository;
import io.twogether.nbe_5_7_2_02team.tag.domain.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TagBrowserSuccessTest extends BrowserTestTemplate {

    @Autowired
    TagRepository tagRepository;

    private final Random random = new Random();

    @Test
    @DisplayName("GET: /api/tags - 모든 태그 반환")
    void getAllTags() throws Exception {
        // given
        List<String> tagNames = new ArrayList<>();
        for (int i = 0; i < random.nextInt(10) + 1; i++) {
            String tagName = "TAG-" + i;
            tagRepository.save(new Tag(tagName));
            tagNames.add(tagName);
        }

        // when & then
        mockMvc.perform(
                get("/api/tags")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.data.tags")
                    .value(containsInAnyOrder(tagNames.toArray(new String[0])))
            );
    }

    @Test
    @DisplayName("GET: /api/tags - 조회된 태그가 없을 경우")
    void getAllTagsNotFound() throws Exception {
        // when & then
        mockMvc.perform(
                get("/api/tags")
            )
            .andDo(print())
            .andExpect(status().isNoContent());
    }
}
