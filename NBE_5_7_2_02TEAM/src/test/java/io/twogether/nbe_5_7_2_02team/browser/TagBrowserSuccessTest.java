package io.twogether.nbe_5_7_2_02team.browser;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.database.rider.core.api.dataset.DataSet;

import io.twogether.nbe_5_7_2_02team.browser.template.BrowserTestTemplate;
import io.twogether.nbe_5_7_2_02team.global.annotation.FlywayReset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@FlywayReset
public class TagBrowserSuccessTest extends BrowserTestTemplate {

    @Test
    @DataSet(
            value = {
                "datasets/v2/member.yml",
                "datasets/v2/post.yml",
                "datasets/v2/tag.yml",
            },
            cleanBefore = true,
            cleanAfter = true)
    @DisplayName("GET: /api/tags - 모든 태그 반환")
    void getAllTags() throws Exception {
        // given
        String[] tagNames = {"TAG-1", "TAG-2"};

        // when & then
        mockMvc.perform(get("/api/tags"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags").value(containsInAnyOrder(tagNames)));
    }

    @Test
    @DisplayName("GET: /api/tags - 조회된 태그가 없을 경우")
    void getAllTagsNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/tags")).andDo(print()).andExpect(status().isNoContent());
    }
}
