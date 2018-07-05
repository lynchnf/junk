package norman.junk.controller;

import java.util.Random;
import norman.junk.domain.Category;
import norman.junk.service.CategoryService;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(CategoryController.class)
public class OldCategoryControllerTest {
    private final Random random = new Random();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService categoryService;

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/categoryList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("categoryList"));
    }

    @Test
    public void loadView() throws Exception {
        Category category = buildExistingCategory();
        BDDMockito.given(categoryService.findCategoryById(category.getId())).willReturn(category);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/category").param("categoryId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("categoryView"));
        resultActions
                .andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(category.getName())));
    }

    @Test
    public void loadEdit() throws Exception {
        Category category = buildExistingCategory();
        BDDMockito.given(categoryService.findCategoryById(category.getId())).willReturn(category);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/categoryEdit")
                .param("categoryId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("categoryEdit"));
        resultActions
                .andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(category.getName())));
    }

    @Test
    public void processEdit() {
        // TODO Write test.
    }

    private Category buildExistingCategory() {
        Long categoryId = Long.valueOf(1);
        String categoryName = RandomStringUtils.randomAlphabetic(50);
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        return category;
    }
}