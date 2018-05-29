package norman.junk.controller;

import java.util.Optional;
import norman.junk.domain.Payable;
import norman.junk.domain.Payee;
import norman.junk.domain.Payment;
import norman.junk.service.PayableService;
import norman.junk.service.PaymentService;
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
@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PayableService payableService;
    @MockBean
    private PaymentService paymentService;

    @Test
    public void loadView() throws Exception {
        Long paymentId = Long.valueOf(1);
        Payment payment = new Payment();
        payment.setId(paymentId);
        Payable payable = new Payable();
        Payee payee = new Payee();
        String payeeNickname = RandomStringUtils.randomAlphabetic(50);
        payee.setNickname(payeeNickname);
        payable.setPayee(payee);
        payment.setPayable(payable);
        Optional<Payment> optionalPayment = Optional.of(payment);
        BDDMockito.given(paymentService.findPaymentById(paymentId)).willReturn(optionalPayment);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payment").param("paymentId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("paymentView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(payeeNickname)));
    }

    @Test
    public void loadViewPaymentNotExist() throws Exception {
        Long paymentId = Long.valueOf(2);
        Optional<Payment> optionalPayment = Optional.empty();
        BDDMockito.given(paymentService.findPaymentById(paymentId)).willReturn(optionalPayment);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payment").param("paymentId", "2");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        resultActions.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"));
    }

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/paymentList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("paymentList"));
    }

    @Test
    public void loadEdit() {
    }

    @Test
    public void processEdit() {
    }
}