package norman.junk.controller;

import norman.junk.domain.Acct;
import norman.junk.domain.Tran;
import norman.junk.service.PatternService;
import norman.junk.service.TranService;
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
@WebMvcTest(TranController.class)
public class OldTranControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TranService tranService;
    @MockBean
    private PatternService patternService;

    @Test
    public void loadView() throws Exception {
        Tran tran = buildExistingTran();
        BDDMockito.given(tranService.findTranById(tran.getId())).willReturn(tran);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tran").param("tranId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("tranView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(tran.getName())));
    }

    @Test
    public void assignCategories() {
        // TODO Write test.
    }

    private Tran buildExistingTran() {
        Long acctId = Long.valueOf(1);
        String acctName = RandomStringUtils.randomAlphabetic(50);
        Acct acct = new Acct();
        acct.setId(acctId);
        acct.setName(acctName);
        Long tranId = Long.valueOf(1);
        String tranName = RandomStringUtils.randomAlphabetic(50);
        Tran tran = new Tran();
        tran.setId(tranId);
        tran.setName(tranName);
        tran.setAcct(acct);
        acct.getTrans().add(tran);
        return tran;
    }
}