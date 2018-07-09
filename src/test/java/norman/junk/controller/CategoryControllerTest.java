package norman.junk.controller;

import norman.junk.FakeDataUtil;
import norman.junk.domain.Category;
import norman.junk.service.CategoryService;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
public class CategoryControllerTest {
    private Long category1Id;
    private Category category1;
    private String category1Name;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService categoryService;

    @Before
    public void setUp() throws Exception {
        category1Id = Long.valueOf(1);
        category1 = FakeDataUtil.buildCategory(category1Id);
        category1Name = category1.getName();
    }

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/categoryList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("categoryList"));
    }

    @Test
    public void loadView() throws Exception {
        Mockito.when(categoryService.findCategoryById(category1Id)).thenReturn(category1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/category").param("categoryId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("categoryView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(category1Name)));
    }

    @Test
    public void loadEdit() throws Exception {
        Mockito.when(categoryService.findCategoryById(category1Id)).thenReturn(category1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/categoryEdit").param("categoryId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("categoryEdit"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(category1Name)));
    }

    @Test
    public void processEdit() throws Exception {
        Mockito.when(categoryService.saveCategory(Mockito.any())).thenReturn(category1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/categoryEdit")
                .param("id", category1Id.toString()).param("version", "0").param("name", category1Name);
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/category?categoryId={categoryId}"));
        resultActions.andExpect(MockMvcResultMatchers.model().attribute("categoryId", "1"));
        resultActions.andExpect(
                MockMvcResultMatchers.flash().attribute("successMessage", StringContains.containsString("id=1")));
    }
}