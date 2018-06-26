package norman.junk.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
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
    private final Random random = new Random();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PayeeService payeeService;
    @MockBean
    private PayableService payableService;

    @Test
    public void loadView() throws Exception {
        Payable payable = buildExistingPayable();
        BDDMockito.given(payableService.findPayableById(payable.getId())).willReturn(Optional.of(payable));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payable").param("payableId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payableView"));
        resultActions.andExpect(
                MockMvcResultMatchers.content().string(StringContains.containsString(payable.getPayee().getName())));
    }

    @Test
    public void loadViewPayableNotExist() throws Exception {
        BDDMockito.given(payableService.findPayableById(Long.valueOf(2))).willReturn(Optional.empty());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payable").param("payableId", "2");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        resultActions.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"));
    }

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payableList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payableList"));
    }

    @Test
    public void loadEdit() throws Exception {
        Payable payable = buildExistingPayable();
        BDDMockito.given(payableService.findPayableById(payable.getId())).willReturn(Optional.of(payable));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payableEdit")
                .param("payableId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payableEdit"));
    }

    @Test
    public void processEdit() throws Exception {
        // TODO Write processEdit test.
    }

    private Payable buildExistingPayable() {
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
        payable.setDueDate(payDueDt);
        payable.setAmountDue(newBalTot);
        payable.setPayee(payee);
        payee.getPayables().add(payable);
        return payable;
    }
}