package norman.junk.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
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
    private final Random random = new Random();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PayableService payableService;
    @MockBean
    private PaymentService paymentService;

    @Test
    public void loadView() throws Exception {
        Payment payment = buildExistingPayment();
        BDDMockito.given(paymentService.findPaymentById(payment.getId())).willReturn(Optional.of(payment));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payment").param("paymentId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("paymentView"));
        resultActions.andExpect(MockMvcResultMatchers.content()
                .string(StringContains.containsString(payment.getPayable().getPayee().getName())));
    }

    @Test
    public void loadViewPaymentNotExist() throws Exception {
        BDDMockito.given(paymentService.findPaymentById(Long.valueOf(2))).willReturn(Optional.empty());
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
    public void loadEdit() throws Exception {
        Payment payment = buildExistingPayment();
        BDDMockito.given(paymentService.findPaymentById(payment.getId())).willReturn(Optional.of(payment));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/paymentEdit")
                .param("paymentId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("paymentEdit"));
        resultActions.andExpect(MockMvcResultMatchers.content()
                .string(StringContains.containsString(payment.getPayable().getPayee().getName())));
    }

    @Test
    public void processEdit() throws Exception {
        // TODO Write processEdit test.
    }

    private Payment buildExistingPayment() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Long payeeId = Long.valueOf(1);
        String payeeName = RandomStringUtils.randomAlphabetic(50);
        String number = RandomStringUtils.randomNumeric(50);
        Payee payee = new Payee();
        payee.setId(payeeId);
        payee.setName(payeeName);
        payee.setNumber(number);
        Long payableId = Long.valueOf(1);
        cal.add(Calendar.DATE, random.nextInt(60) - 30);
        Date payDueDt = cal.getTime();
        BigDecimal newBalTot = BigDecimal.valueOf(random.nextInt(10000), 2);
        Payable payable = new Payable();
        payable.setId(payableId);
        payable.setPaymentDueDate(payDueDt);
        payable.setNewBalanceTotal(newBalTot);
        payable.setPayee(payee);
        payee.getPayables().add(payable);
        Long paymentId = Long.valueOf(1);
        cal.add(Calendar.DATE, random.nextInt(7) - 3);
        Date paidDate = cal.getTime();
        BigDecimal amountPaid = BigDecimal.valueOf(random.nextInt(10000), 2);
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setPaidDate(paidDate);
        payment.setAmountPaid(amountPaid);
        payment.setPayable(payable);
        payable.getPayments().add(payment);
        return payment;
    }
}