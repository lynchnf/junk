package norman.junk.controller;

import java.util.Optional;
import norman.junk.domain.Acct;
import norman.junk.domain.Tran;
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
public class TranControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TranService tranService;

    @Test
    public void loadView() throws Exception {
        Long tranId = Long.valueOf(1);
        Tran tran = new Tran();
        tran.setId(tranId);
        Acct acct = new Acct();
        String acctName = RandomStringUtils.randomAlphabetic(50);
        acct.setName(acctName);
        tran.setAcct(acct);
        Optional<Tran> optionalTran = Optional.of(tran);
        BDDMockito.given(tranService.findTranById(tranId)).willReturn(optionalTran);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/tran").param("tranId", "1"));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("tranView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(acctName)));
    }

    @Test
    public void loadViewTranNotExist() throws Exception {
        Long tranId = Long.valueOf(2);
        Optional<Tran> optionalTran = Optional.empty();
        BDDMockito.given(tranService.findTranById(tranId)).willReturn(optionalTran);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tran").param("tranId", "2");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        resultActions.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"));
    }
}