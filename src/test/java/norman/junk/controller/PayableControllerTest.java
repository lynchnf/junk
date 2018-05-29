package norman.junk.controller;

import java.util.Optional;
import norman.junk.domain.Payable;
import norman.junk.domain.Payee;
import norman.junk.service.PayableService;
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
@WebMvcTest(PayableController.class)
public class PayableControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PayeeService payeeService;
    @MockBean
    private PayableService payableService;

    @Test
    public void loadView() throws Exception {
        Long payableId = Long.valueOf(1);
        Payable payable = new Payable();
        payable.setId(payableId);
        Payee payee = new Payee();
        String payeeNickname = RandomStringUtils.randomAlphabetic(50);
        payee.setNickname(payeeNickname);
        payable.setPayee(payee);
        Optional<Payable> optionalPayable = Optional.of(payable);
        BDDMockito.given(payableService.findPayableById(payableId)).willReturn(optionalPayable);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/payable").param("payableId", "1"));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payableView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(payeeNickname)));
    }

    @Test
    public void loadViewPayableNotExist() throws Exception {
        Long payableId = Long.valueOf(2);
        Optional<Payable> optionalPayable = Optional.empty();
        BDDMockito.given(payableService.findPayableById(payableId)).willReturn(optionalPayable);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payable").param("payableId", "2");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        resultActions.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"));
    }

    @Test
    public void loadList() {
    }

    @Test
    public void loadEdit() {
    }

    @Test
    public void processEdit() {
    }
}