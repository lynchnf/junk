package norman.junk.controller;

import norman.junk.FakeDataUtil;
import norman.junk.domain.Payee;
import norman.junk.service.PayeeService;
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
@WebMvcTest(PayeeController.class)
public class PayeeControllerTest {
    private Long payee1Id;
    private Payee payee1;
    private String payee1Name;
    private String payee1Number;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PayeeService payeeService;

    @Before
    public void setUp() throws Exception {
        payee1Id = Long.valueOf(1);
        payee1 = FakeDataUtil.buildPayee(1);
        payee1Name = payee1.getName();
        payee1Number = payee1.getNumber();
    }

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payeeList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payeeList"));
    }

    @Test
    public void loadView() throws Exception {
        Mockito.when(payeeService.findPayeeById(payee1Id)).thenReturn(payee1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payee").param("payeeId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payeeView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(payee1Name)));
    }

    @Test
    public void loadEdit() throws Exception {
        Mockito.when(payeeService.findPayeeById(payee1Id)).thenReturn(payee1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payeeEdit").param("payeeId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payeeEdit"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(payee1Name)));
    }

    @Test
    public void processEdit() throws Exception {
        Mockito.when(payeeService.savePayee(Mockito.any())).thenReturn(payee1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/payeeEdit")
                .param("id", payee1Id.toString()).param("version", "0").param("name", payee1Name)
                .param("number", payee1Number);
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/payee?payeeId={payeeId}"));
        resultActions.andExpect(MockMvcResultMatchers.model().attribute("payeeId", "1"));
        resultActions.andExpect(
                MockMvcResultMatchers.flash().attribute("successMessage", StringContains.containsString("id=1")));
    }
}