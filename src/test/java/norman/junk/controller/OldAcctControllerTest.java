package norman.junk.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.AcctType;
import norman.junk.domain.DataFile;
import norman.junk.domain.DataLine;
import norman.junk.domain.Tran;
import norman.junk.domain.TranType;
import norman.junk.service.AcctService;
import norman.junk.service.DataFileService;
import norman.junk.service.OfxAcct;
import norman.junk.service.OfxInst;
import norman.junk.service.OfxParseResponse;
import norman.junk.service.OfxParseService;
import norman.junk.service.OfxStmtTran;
import norman.junk.service.TranService;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(AcctController.class)
public class OldAcctControllerTest {
    private final Random random = new Random();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AcctService acctService;
    @MockBean
    private DataFileService dataFileService;
    @MockBean
    private OfxParseService ofxParseService;
    @MockBean
    private TranService tranService;

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/acctList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("acctList"));
    }

    @Test
    public void loadView() throws Exception {
        Acct acct = buildExistingAcct();
        BDDMockito.given(acctService.findAcctById(acct.getId())).willReturn(acct);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/acct").param("acctId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("acctView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(acct.getName())));
    }

    @Test
    public void loadEdit() throws Exception {
        Acct acct = buildExistingAcct();
        BDDMockito.given(acctService.findAcctById(acct.getId())).willReturn(acct);
        AcctNbr acctNbr = acct.getAcctNbrs().iterator().next();
        BDDMockito.given(acctService.findCurrentAcctNbrByAcctId(acct.getId())).willReturn(acctNbr);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/acctEdit").param("acctId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("acctEdit"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(acct.getName())));
    }

    @Test
    public void processEdit() {
        // TODO Write test.
    }

    @Test
    public void loadUpload() {
        // TODO Write test.
    }

    @Test
    public void processUpload() throws Exception {
        String name = "multipartFile";
        String originalFilename = "activity.ofx";
        String contentType = "application/octet-stream";
        Acct acct = buildExistingAcct();
        AcctNbr acctNbr = acct.getAcctNbrs().iterator().next();
        Tran debit = buildDebitTran();
        Tran credit = buildCreditTran();
        List<String> ofxFile = buildOfxFile(acct, debit, credit);
        StringBuilder sb = new StringBuilder();
        for (String ofxLine : ofxFile) {
            sb.append(String.format("%s%n", ofxLine));
        }
        byte[] content = sb.toString().getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(name, originalFilename, contentType, content);
        DataFile dataFile = buildDataFile(ofxFile);
        BDDMockito.given(dataFileService.saveDataFile(ArgumentMatchers.isA(DataFile.class))).willReturn(dataFile);
        OfxParseResponse ofxParseResponse = buildOfxParseResponse(acct, debit, credit);
        BDDMockito.given(ofxParseService.parseUploadedFile(ArgumentMatchers.isA(DataFile.class)))
                .willReturn(ofxParseResponse);
        String ofxFid = acct.getFid();
        String ofxAcctId = acctNbr.getNumber();
        List<AcctNbr> acctNbrsByFidAndNumber = new ArrayList<>();
        acctNbrsByFidAndNumber.add(acctNbr);
        BDDMockito.given(acctService.findAcctNbrsByFidAndNumber(ofxFid, ofxAcctId)).willReturn(acctNbrsByFidAndNumber);
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload("/dataFileUpload")
                .file(multipartFile);
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        resultActions.andExpect(MockMvcResultMatchers.flash()
                .attribute("successMessage", StringContains.containsString("2 Transactions")));
    }

    @Test
    public void processUploadAcctNotExist() throws Exception {
        String name = "multipartFile";
        String originalFilename = "activity.ofx";
        String contentType = "application/octet-stream";
        Acct acct = buildExistingAcct();
        Tran debit = buildDebitTran();
        Tran credit = buildCreditTran();
        List<String> ofxFile = buildOfxFile(acct, debit, credit);
        StringBuilder sb = new StringBuilder();
        for (String ofxLine : ofxFile) {
            sb.append(String.format("%s%n", ofxLine));
        }
        byte[] content = sb.toString().getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(name, originalFilename, contentType, content);
        DataFile dataFile = buildDataFile(ofxFile);
        BDDMockito.given(dataFileService.saveDataFile(ArgumentMatchers.isA(DataFile.class))).willReturn(dataFile);
        OfxParseResponse ofxParseResponse = buildOfxParseResponse(acct, debit, credit);
        BDDMockito.given(ofxParseService.parseUploadedFile(ArgumentMatchers.isA(DataFile.class)))
                .willReturn(ofxParseResponse);
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload("/dataFileUpload")
                .file(multipartFile);
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("acctEdit"));
    }

    @Test
    public void processUploadTransExist() throws Exception {
        String name = "multipartFile";
        String originalFilename = "activity.ofx";
        String contentType = "application/octet-stream";
        Acct acct = buildExistingAcct();
        AcctNbr acctNbr = acct.getAcctNbrs().iterator().next();
        Tran debit = buildDebitTran();
        Tran credit = buildCreditTran();
        List<String> ofxFile = buildOfxFile(acct, debit, credit);
        StringBuilder sb = new StringBuilder();
        for (String ofxLine : ofxFile) {
            sb.append(String.format("%s%n", ofxLine));
        }
        byte[] content = sb.toString().getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(name, originalFilename, contentType, content);
        DataFile dataFile = buildDataFile(ofxFile);
        BDDMockito.given(dataFileService.saveDataFile(ArgumentMatchers.isA(DataFile.class))).willReturn(dataFile);
        OfxParseResponse ofxParseResponse = buildOfxParseResponse(acct, debit, credit);
        BDDMockito.given(ofxParseService.parseUploadedFile(ArgumentMatchers.isA(DataFile.class)))
                .willReturn(ofxParseResponse);
        String ofxFid = acct.getFid();
        String ofxAcctId = acctNbr.getNumber();
        List<AcctNbr> acctNbrsByFidAndNumber = new ArrayList<>();
        acctNbrsByFidAndNumber.add(acctNbr);
        BDDMockito.given(acctService.findAcctNbrsByFidAndNumber(ofxFid, ofxAcctId)).willReturn(acctNbrsByFidAndNumber);
        List<Tran> debitTrans = new ArrayList<>();
        debitTrans.add(debit);
        List<Tran> creditTrans = new ArrayList<>();
        creditTrans.add(credit);
        BDDMockito.given(acctService.findTransByAcctIdAndFitId(acct.getId(), debit.getFitId())).willReturn(debitTrans);
        BDDMockito.given(acctService.findTransByAcctIdAndFitId(acct.getId(), credit.getFitId()))
                .willReturn(creditTrans);
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload("/dataFileUpload")
                .file(multipartFile);
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        resultActions.andExpect(MockMvcResultMatchers.flash()
                .attribute("successMessage", StringContains.containsString("0 Transactions")));
    }

    @Test
    public void processUploadTransTwice() throws Exception {
        String name = "multipartFile";
        String originalFilename = "activity.ofx";
        String contentType = "application/octet-stream";
        Acct acct = buildExistingAcct();
        AcctNbr acctNbr = acct.getAcctNbrs().iterator().next();
        Tran debit = buildDebitTran();
        Tran credit = buildCreditTran();
        List<String> ofxFile = buildOfxFile(acct, debit, debit, credit, credit);
        StringBuilder sb = new StringBuilder();
        for (String ofxLine : ofxFile) {
            sb.append(String.format("%s%n", ofxLine));
        }
        byte[] content = sb.toString().getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(name, originalFilename, contentType, content);
        DataFile dataFile = buildDataFile(ofxFile);
        BDDMockito.given(dataFileService.saveDataFile(ArgumentMatchers.isA(DataFile.class))).willReturn(dataFile);
        OfxParseResponse ofxParseResponse = buildOfxParseResponse(acct, debit, debit, credit, credit);
        BDDMockito.given(ofxParseService.parseUploadedFile(ArgumentMatchers.isA(DataFile.class)))
                .willReturn(ofxParseResponse);
        String ofxFid = acct.getFid();
        String ofxAcctId = acctNbr.getNumber();
        List<AcctNbr> acctNbrsByFidAndNumber = new ArrayList<>();
        acctNbrsByFidAndNumber.add(acctNbr);
        BDDMockito.given(acctService.findAcctNbrsByFidAndNumber(ofxFid, ofxAcctId)).willReturn(acctNbrsByFidAndNumber);
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload("/dataFileUpload")
                .file(multipartFile);
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        resultActions.andExpect(MockMvcResultMatchers.flash()
                .attribute("successMessage", StringContains.containsString("2 Transactions")));
    }

    @Test
    public void loadReconcile() {
        // TODO Write test.
    }

    @Test
    public void processReconcile() {
        // TODO Write test.
    }

    private Acct buildExistingAcct() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Long acctId = Long.valueOf(1);
        String acctName = RandomStringUtils.randomAlphabetic(50);
        String organization = RandomStringUtils.randomAlphabetic(50);
        String fid = RandomStringUtils.randomNumeric(4);
        cal.add(Calendar.DATE, random.nextInt(100) * -1);
        Date beginDate = cal.getTime();
        BigDecimal beginBalance = BigDecimal.valueOf(random.nextInt(100000), 2);
        AcctType acctType = AcctType.values()[random.nextInt(AcctType.values().length)];
        Acct acct = new Acct();
        acct.setId(acctId);
        acct.setName(acctName);
        acct.setOrganization(organization);
        acct.setFid(fid);
        acct.setBeginDate(beginDate);
        acct.setBeginBalance(beginBalance);
        acct.setType(acctType);
        Long acctNbrId = Long.valueOf(1);
        String number = RandomStringUtils.randomNumeric(20);
        AcctNbr acctNbr = new AcctNbr();
        acctNbr.setId(acctNbrId);
        acctNbr.setNumber(number);
        acctNbr.setAcct(acct);
        acct.getAcctNbrs().add(acctNbr);
        return acct;
    }

    private Tran buildDebitTran() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Tran tran = new Tran();
        tran.setId(Long.valueOf(1));
        tran.setType(TranType.DEBIT);
        tran.setPostDate(cal.getTime());
        tran.setAmount(BigDecimal.valueOf(random.nextInt(100000) * -1, 2));
        tran.setFitId(RandomStringUtils.randomNumeric(20));
        tran.setName("ELECTRONIC WITHDRAWAL BIG MORTGAGE");
        return tran;
    }

    private Tran buildCreditTran() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Tran tran = new Tran();
        tran.setId(Long.valueOf(2));
        tran.setType(TranType.CREDIT);
        tran.setPostDate(cal.getTime());
        tran.setAmount(BigDecimal.valueOf(random.nextInt(100000), 2));
        tran.setFitId(RandomStringUtils.randomNumeric(20));
        tran.setName("ELECTRONIC DEPOSIT FAT PAYCHECK");
        return tran;
    }

    private List<String> buildOfxFile(Acct acct, Tran... trans) {
        List<String> ofxFile = new ArrayList<>();
        ofxFile.add(String.format("<OFX>"));
        ofxFile.add(String.format("    <FI>"));
        ofxFile.add(String.format("        <ORG>%s", acct.getOrganization()));
        ofxFile.add(String.format("        <FID>%s", acct.getFid()));
        ofxFile.add(String.format("    </FI>"));
        ofxFile.add(String.format("    <BANKACCTFROM>"));
        ofxFile.add(String.format("        <ACCTID>%s", acct.getAcctNbrs().iterator().next().getNumber()));
        ofxFile.add(String.format("        <ACCTTYPE>%s", acct.getType()));
        ofxFile.add(String.format("    </BANKACCTFROM>"));
        ofxFile.add(String.format("    <BANKTRANLIST>"));
        for (Tran tran : trans) {
            ofxFile.add(String.format("        <STMTTRN>"));
            ofxFile.add(String.format("            <TRNTYPE>%s", tran.getType()));
            ofxFile.add(String.format("            <DTPOSTED>%1$tY%1$tm%1$td120000.000", tran.getPostDate()));
            ofxFile.add(String.format("            <TRNAMT>%.2f", tran.getAmount()));
            ofxFile.add(String.format("            <FITID>%s", tran.getFitId()));
            ofxFile.add(String.format("            <NAME>%s", tran.getName()));
            ofxFile.add(String.format("        </STMTTRN>"));
        }
        ofxFile.add(String.format("    </BANKTRANLIST>"));
        ofxFile.add(String.format("</OFX>"));
        return ofxFile;
    }

    private DataFile buildDataFile(List<String> ofxFile) {
        DataFile dataFile = new DataFile();
        dataFile.setId(Long.valueOf(1));
        dataFile.setOriginalFilename("activity.ofx");
        dataFile.setContentType("application/octet-stream");
        dataFile.setSize(Long.valueOf(700));
        dataFile.setUploadTimestamp(new Date());
        int i = 0;
        for (String ofxLine : ofxFile) {
            DataLine dataLine = new DataLine();
            dataLine.setSeq(i++);
            dataFile.setId(Long.valueOf(i));
            dataLine.setText(ofxLine);
            dataLine.setDataFile(dataFile);
            dataFile.getDataLines().add(dataLine);
        }
        return dataFile;
    }

    private OfxParseResponse buildOfxParseResponse(Acct acct, Tran... trans) {
        OfxParseResponse ofxParseResponse = new OfxParseResponse();
        OfxInst ofxInst = new OfxInst();
        ofxInst.setOrganization(acct.getOrganization());
        ofxInst.setFid(acct.getFid());
        ofxParseResponse.setOfxInst(ofxInst);
        OfxAcct ofxAcct = new OfxAcct();
        ofxAcct.setAcctId(acct.getAcctNbrs().iterator().next().getNumber());
        ofxAcct.setType(acct.getType());
        ofxParseResponse.setOfxAcct(ofxAcct);
        for (Tran tran : trans) {
            OfxStmtTran ofxStmtTran = new OfxStmtTran();
            ofxStmtTran.setType(tran.getType());
            ofxStmtTran.setPostDate(tran.getPostDate());
            ofxStmtTran.setAmount(tran.getAmount());
            ofxStmtTran.setFitId(tran.getFitId());
            ofxStmtTran.setName(tran.getName());
            ofxParseResponse.getOfxStmtTrans().add(ofxStmtTran);
        }
        return ofxParseResponse;
    }
}