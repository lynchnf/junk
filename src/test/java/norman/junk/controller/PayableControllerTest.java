package norman.junk.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import norman.junk.FakeDataUtil;
import norman.junk.domain.Payable;
import norman.junk.domain.Payee;
import norman.junk.service.PayableService;
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
@WebMvcTest(PayableController.class)
public class PayableControllerTest {
    private DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
    private Long payable1Id;
    private Payee payee1;
    private Payable payable1;
    private Date payable1DueDate;
    private BigDecimal payable1AmountDue;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PayableService payableService;
    @MockBean
    private PayeeService payeeService;

    @Before
    public void setUp() throws Exception {
        payable1Id = Long.valueOf(1);
        payee1 = FakeDataUtil.buildPayee(1);
        payable1 = FakeDataUtil.buildPayable(payee1, 1, null);
        payable1DueDate = payable1.getDueDate();
        payable1AmountDue = payable1.getAmountDue();
    }

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payableList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payableList"));
    }

    @Test
    public void loadView() throws Exception {
        Mockito.when(payableService.findPayableById(payable1Id)).thenReturn(payable1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payable").param("payableId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payableView"));
        String formattedAmount = String.format("%.2f", payable1AmountDue);
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(formattedAmount)));
    }

    @Test
    public void loadEdit() throws Exception {
        Mockito.when(payableService.findPayableById(payable1Id)).thenReturn(payable1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/payableEdit")
                .param("payableId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("payableEdit"));
        String formattedAmount = String.format("%.2f", payable1AmountDue);
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(formattedAmount)));
    }

    @Test
    public void processEdit() throws Exception {
        Mockito.when(payableService.savePayable(Mockito.any())).thenReturn(payable1);
        String formattedDate = dateFormat.format(payable1DueDate);
        String formattedAmount = String.format("%.2f", payable1AmountDue);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/payableEdit")
                .param("id", payable1Id.toString()).param("version", "0").param("payeeId", "1")
                .param("dueDate", formattedDate).param("amountDue", formattedAmount);
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/payable?payableId={payableId}"));
        resultActions.andExpect(MockMvcResultMatchers.model().attribute("payableId", "1"));
        resultActions.andExpect(
                MockMvcResultMatchers.flash().attribute("successMessage", StringContains.containsString("id=1")));
    }
}