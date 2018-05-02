package norman.junk.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.Tran;
import norman.junk.repository.AcctNbrRepository;
import norman.junk.repository.AcctRepository;
import norman.junk.repository.TranRepository;

@RunWith(SpringRunner.class)
public class AcctServiceTest {
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
    private Acct acct0 = new Acct();
    private Acct acct1 = new Acct();
    private Acct acct2 = new Acct();
    private Acct acct3 = new Acct();
    private Acct acct4 = new Acct();
    private AcctNbr acctNbr21 = new AcctNbr();
    private AcctNbr acctNbr22 = new AcctNbr();
    private AcctNbr acctNbr31 = new AcctNbr();
    private Tran tran201 = new Tran();
    private Tran tran202 = new Tran();
    private Tran tran203 = new Tran();

    @TestConfiguration
    static class AcctServiceTestConfiguration {
        @Bean
        public AcctService acctService() {
            return new AcctService();
        }
    }

    @Autowired
    private AcctService acctService;
    @MockBean
    private AcctRepository acctRepository;
    @MockBean
    private AcctNbrRepository acctNbrRepository;
    @MockBean
    private TranRepository tranRepository;

    @Before
    public void setUp() throws Exception {
        // New account before saving.
        acct0.setName("New Account");
        // New account after saving.
        acct1.setId(1L);
        acct1.setName("New Account");
        // Existing account.
        acct2.setId(2L);
        acct2.setName("Existing account");
        // Existing account with FID.
        acct3.setId(3L);
        acct3.setName("Existing account with FID");
        acct3.setFid("123");
        // Another account with same FID.
        acct4.setId(4L);
        acct4.setName("Another account with same FID");
        acct4.setFid("123");
        // Attach account numbers to existing account.
        acctNbr21.setId(21L);
        acctNbr21.setNumber("0101");
        acctNbr21.setEffDate(DF.parse("2018-01-01"));
        acctNbr21.setAcct(acct2);
        acct2.getAcctNbrs().add(acctNbr21);
        acctNbr22.setId(22L);
        acctNbr22.setNumber("0202");
        acctNbr22.setEffDate(DF.parse("2018-02-02"));
        acctNbr22.setAcct(acct2);
        acct2.getAcctNbrs().add(acctNbr22);
        // Attach account number to account with FID.
        acctNbr31.setId(31L);
        acctNbr31.setNumber("0301");
        acctNbr31.setEffDate(DF.parse("2018-03-01"));
        acctNbr31.setAcct(acct3);
        acct3.getAcctNbrs().add(acctNbr31);
        // Attach transactions to existing account.
        tran201.setId(201L);
        tran201.setPostDate(DF.parse("2018-01-10"));
        tran201.setAmount(BigDecimal.valueOf(123L));
        tran201.setFitId("12345678");
        tran201.setAcct(acct2);
        acct2.getTrans().add(tran201);
        tran202.setId(202L);
        tran202.setPostDate(DF.parse("2018-01-20"));
        tran202.setAmount(BigDecimal.valueOf(456L));
        tran202.setFitId("90123456");
        tran202.setAcct(acct2);
        acct2.getTrans().add(tran202);
        tran203.setId(203L);
        tran203.setPostDate(DF.parse("2018-01-30"));
        tran203.setAmount(BigDecimal.valueOf(-78L));
        tran203.setFitId("78901234");
        tran203.setAcct(acct2);
        acct2.getTrans().add(tran203);
        Mockito.when(acctRepository.save(acct0)).thenReturn(acct1);
        Mockito.when(acctRepository.findById(acct2.getId())).thenReturn(Optional.of(acct2));
        List<Acct> acctsByFid = Arrays.asList(acct3, acct4);
        Mockito.when(acctRepository.findByFid(acct3.getFid())).thenReturn(acctsByFid);
        List<Acct> acctsAll = Arrays.asList(acct1, acct2, acct3, acct4);
        Mockito.when(acctRepository.findAll()).thenReturn(acctsAll);
        List<AcctNbr> acctnbrsCurrent = Arrays.asList(acctNbr22, acctNbr21);
        Mockito.when(acctNbrRepository.findTopByAcct_IdOrderByEffDateDesc(acct2.getId())).thenReturn(acctnbrsCurrent);
        List<AcctNbr> acctnbrsByFid = Arrays.asList(acctNbr31);
        Mockito.when(acctNbrRepository.findByAcct_FidAndNumber(acct3.getFid(), acctNbr31.getNumber())).thenReturn(acctnbrsByFid);
        List<Tran> transByFitId = Arrays.asList(tran202);
        Mockito.when(tranRepository.findByAcct_IdAndFitId(acct2.getId(), tran202.getFitId())).thenReturn(transByFitId);
    }

    @After
    public void tearDown() throws Exception {}

    @Test
    public void saveAcct() {
        Acct acct = acctService.saveAcct(acct0);
    }

    @Test
    public void findAcctById() {
        Optional<Acct> optionalAcct = acctService.findAcctById(2L);
    }

    @Test
    public void findAllAccts() {
        Iterable<Acct> accts = acctService.findAllAccts();
    }

    @Test
    public void findAcctsByFid() {
        List<Acct> accts = acctService.findAcctsByFid("123");
    }

    @Test
    public void findCurrentAcctNbrByAcctId() {
        Optional<AcctNbr> optionalAcctNbr = acctService.findCurrentAcctNbrByAcctId(2L);
        assertTrue(optionalAcctNbr.isPresent());
        assertEquals("0202", optionalAcctNbr.get().getNumber());
    }

    @Test
    public void findAcctNbrsByFidAndNumber() {
        List<AcctNbr> acctNbrs = acctService.findAcctNbrsByFidAndNumber("123", "0301");
    }

    @Test
    public void findTransByAcctIdAndFitId() {
        List<Tran> trans = acctService.findTransByAcctIdAndFitId(2L, "90123456");
    }
}