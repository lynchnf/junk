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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void loadDashboard() throws Exception {
        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk());
    }
}