package norman.junk.controller;

import java.util.Optional;
import norman.junk.domain.Acct;
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
        Long acctId = Long.valueOf(1);
        Acct acct = new Acct();
        acct.setId(acctId);
        String acctName = RandomStringUtils.randomAlphabetic(50);
        acct.setName(acctName);
        Optional<Acct> optionalAcct = Optional.of(acct);
        BDDMockito.given(acctService.findAcctById(acctId)).willReturn(optionalAcct);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/acct").param("acctId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("acctView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(acctName)));
    }

    @Test
    public void loadViewAcctNotExist() throws Exception {
        Long acctId = Long.valueOf(2);
        Optional<Acct> optionalAcct = Optional.empty();
        BDDMockito.given(acctService.findAcctById(acctId)).willReturn(optionalAcct);
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
    }

    @Test
    public void processEdit() throws Exception {
    }

    @Test
    public void loadUpload() throws Exception {
    }

    @Test
    public void processUpload() throws Exception {
    }
}