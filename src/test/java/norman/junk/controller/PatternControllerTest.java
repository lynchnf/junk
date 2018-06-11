package norman.junk.controller;

import norman.junk.service.CategoryService;
import norman.junk.service.PatternService;
import org.junit.Test;
import org.junit.runner.RunWith;
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
@WebMvcTest(PatternController.class)
public class PatternControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PatternService patternService;
    @MockBean
    private CategoryService categoryService;

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/patternList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("patternList"));
    }

    @Test
    public void loadEdit() throws Exception {
        // TODO Write loadEdit test.
    }

    @Test
    public void processEdit() throws Exception {
        // TODO Write processEdit test.
    }

    @Test
    public void loadCategoryDropDown() throws Exception {
        // TODO Write loadCategoryDropDown test.
    }
}