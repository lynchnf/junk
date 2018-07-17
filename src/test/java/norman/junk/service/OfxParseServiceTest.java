package norman.junk.service;

import java.util.List;
import norman.junk.FakeDataUtil;
import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.DataFile;
import norman.junk.domain.Tran;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class OfxParseServiceTest {
    private String acct1Fid;
    private String tran1Name;
    private DataFile dataFile1;

    @TestConfiguration
    static class OfxParseServiceTestConfiguration {
        @Bean
        public OfxParseService ofxParseService() {
            return new OfxParseService();
        }
    }

    @Autowired
    private OfxParseService ofxParseService;

    @Before
    public void setUp() throws Exception {
        Acct acct = FakeDataUtil.buildAcct(1);
        acct1Fid = acct.getFid();
        AcctNbr acctNbr = FakeDataUtil.buildAcctNbr(acct, 2);
        Tran tran = FakeDataUtil.buildTran(acct, 3, null);
        tran1Name = tran.getName();
        List<String> ofxFile = FakeDataUtil.buildOfxFile(acct, tran);
        dataFile1 = FakeDataUtil.buildDataFile(ofxFile, 4, 5);
    }

    @Test
    public void parseUploadedFile() throws Exception {
        OfxParseResponse ofxParseResponse = ofxParseService.parseUploadedFile(dataFile1);
        assertEquals(acct1Fid, ofxParseResponse.getOfxInst().getFid());
        List<OfxStmtTran> ofxStmtTrans = ofxParseResponse.getOfxStmtTrans();
        assertEquals(1, ofxStmtTrans.size());
        OfxStmtTran ofxStmtTran = ofxStmtTrans.iterator().next();
        assertEquals(tran1Name, ofxStmtTran.getName());
    }
}