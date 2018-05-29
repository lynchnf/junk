package norman.junk.controller;

import java.util.Optional;
import norman.junk.domain.Payee;
import norman.junk.service.PayeeService;
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
@WebMvcTest(PayeeController.class)
public class PayeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PayeeService payeeService;

    @Test
    public void loadView() throws Exception {
        Long payeeId = Long.valueOf(1);
        Payee payee = new Payee();
        payee.setId(payeeId);
        String payeeNickname = RandomStringUtils.randomAlphabetic(50);
        payee.setNickname(payeeNickname);
        Optional<Payee> optionalPayee = Optional.of(payee);
        BDDMockito.given(payeeService.findPayeeById(payeeId)).willReturn(optionalPayee);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payee").param("payeeId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payeeView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(payeeNickname)));
    }

    @Test
    public void loadViewPayeeNotExist() throws Exception {
        Long payeeId = Long.valueOf(2);
        Optional<Payee> optionalPayee = Optional.empty();
        BDDMockito.given(payeeService.findPayeeById(payeeId)).willReturn(optionalPayee);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payee").param("payeeId", "2");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        resultActions.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"));
    }

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payeeList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payeeList"));
    }

    @Test
    public void loadEdit() {
    }

    @Test
    public void processEdit() {
    }
}