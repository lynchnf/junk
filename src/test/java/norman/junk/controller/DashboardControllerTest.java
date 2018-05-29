package norman.junk.controller;

import norman.junk.service.AcctService;
import norman.junk.service.DataFileService;
import norman.junk.service.PayeeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(DashboardController.class)
public class DashboardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AcctService acctService;
    @MockBean
    private DataFileService dataFileService;
    @MockBean
    private PayeeService payeeService;

    @Test
    public void loadView() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/"));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("index"));
    }
}