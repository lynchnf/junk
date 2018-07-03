package norman.junk.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import norman.junk.FakeDataUtil;
import norman.junk.domain.Payable;
import norman.junk.domain.Payee;
import norman.junk.domain.Payment;
import norman.junk.service.PayableService;
import norman.junk.service.PaymentService;
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
@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {
    private DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
    private Long payment1Id;
    private Payee payee1;
    private Payable payable1;
    private Payment payment1;
    private Date payment1PaidDate;
    private BigDecimal payment1AmountPaid;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PaymentService paymentService;
    @MockBean
    private PayableService payableService;

    @Before
    public void setUp() throws Exception {
        payment1Id = Long.valueOf(1);
        payee1 = FakeDataUtil.buildPayee(1);
        payable1 = FakeDataUtil.buildPayable(payee1, 1, null);
        payment1 = FakeDataUtil.buildPartialPayment(payable1, 1);
        payment1PaidDate = payment1.getPaidDate();
        payment1AmountPaid = payment1.getAmountPaid();
    }

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/paymentList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("paymentList"));
    }

    @Test
    public void loadView() throws Exception {
        Mockito.when(paymentService.findPaymentById(payment1Id)).thenReturn(payment1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payment").param("paymentId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("paymentView"));
        String formattedAmount = String.format("%.2f", payment1AmountPaid);
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(formattedAmount)));
    }

    @Test
    public void loadEdit() throws Exception {
        Mockito.when(paymentService.findPaymentById(payment1Id)).thenReturn(payment1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/paymentEdit")
                .param("paymentId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("paymentEdit"));
        String formattedAmount = String.format("%.2f", payment1AmountPaid);
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(formattedAmount)));
    }

    @Test
    public void processEdit() throws Exception {
        Mockito.when(paymentService.savePayment(Mockito.any())).thenReturn(payment1);
        String formattedDate = dateFormat.format(payment1PaidDate);
        String formattedAmount = String.format("%.2f", payment1AmountPaid);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/paymentEdit")
                .param("id", payment1Id.toString()).param("version", "0").param("payableId", "1")
                .param("paidDate", formattedDate).param("amountPaid", formattedAmount);
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/payment?paymentId={paymentId}"));
        resultActions.andExpect(MockMvcResultMatchers.model().attribute("paymentId", "1"));
        resultActions.andExpect(
                MockMvcResultMatchers.flash().attribute("successMessage", StringContains.containsString("id=1")));
    }
}