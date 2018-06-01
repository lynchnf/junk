package norman.junk.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.AcctType;
import norman.junk.service.AcctService;
import norman.junk.service.DataFileService;
import norman.junk.service.OfxParseService;
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
@WebMvcTest(AcctController.class)
public class AcctControllerTest {
    private final Random random = new Random();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AcctService acctService;
    @MockBean
    private DataFileService dataFileService;
    @MockBean
    private OfxParseService ofxParseService;

    @Test
    public void loadView() throws Exception {
        Acct acct = buildExistingAcct();
        BDDMockito.given(acctService.findAcctById(acct.getId())).willReturn(Optional.of(acct));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/acct").param("acctId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("acctView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(acct.getName())));
    }

    @Test
    public void loadViewAcctNotExist() throws Exception {
        BDDMockito.given(acctService.findAcctById(Long.valueOf(2))).willReturn(Optional.empty());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/acct").param("acctId", "2");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        resultActions.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"));
    }

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/acctList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("acctList"));
    }

    @Test
    public void loadEdit() throws Exception {
        Acct acct = buildExistingAcct();
        BDDMockito.given(acctService.findAcctById(acct.getId())).willReturn(Optional.of(acct));
        AcctNbr acctNbr = acct.getAcctNbrs().iterator().next();
        BDDMockito.given(acctService.findCurrentAcctNbrByAcctId(acct.getId())).willReturn(Optional.of(acctNbr));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/acctEdit").param("acctId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("acctEdit"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(acct.getName())));
    }

    @Test
    public void processEdit() throws Exception {
        // TODO Write processEdit test.
    }

    @Test
    public void loadUpload() throws Exception {
        // TODO Write loadUpload test.
    }

    @Test
    public void processUpload() throws Exception {
        // TODO Write processUpload test.
    }

    private Acct buildExistingAcct() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Long acctId = Long.valueOf(1);
        String acctName = RandomStringUtils.randomAlphabetic(50);
        cal.add(Calendar.DATE, random.nextInt(100) * -1);
        Date beginDate = cal.getTime();
        BigDecimal beginBalance = BigDecimal.valueOf(random.nextInt(100000), 2);
        AcctType acctType = AcctType.values()[random.nextInt(AcctType.values().length)];
        Acct acct = new Acct();
        acct.setId(acctId);
        acct.setName(acctName);
        acct.setBeginDate(beginDate);
        acct.setBeginBalance(beginBalance);
        acct.setType(acctType);
        Long acctNbrId = Long.valueOf(1);
        String number = RandomStringUtils.randomNumeric(50);
        AcctNbr acctNbr = new AcctNbr();
        acctNbr.setId(acctNbrId);
        acctNbr.setNumber(number);
        acctNbr.setAcct(acct);
        acct.getAcctNbrs().add(acctNbr);
        return acct;
    }
}