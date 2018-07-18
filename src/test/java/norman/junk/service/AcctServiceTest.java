package norman.junk.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import norman.junk.FakeDataUtil;
import norman.junk.JunkNotFoundException;
import norman.junk.JunkOptimisticLockingException;
import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.Tran;
import norman.junk.repository.AcctNbrRepository;
import norman.junk.repository.AcctRepository;
import norman.junk.repository.TranRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
public class AcctServiceTest {
    private Long acct1Id;
    private Long acct2Id;
    private Acct acct1;
    private String acct1Name;
    private String acct1Fid;
    private Long acctNbr1Id;
    private AcctNbr acctNbr1;
    private String acctNbr1Number;
    private Tran tran1;
    private String tran1FitId;
    private String tran1name;
    private BigDecimal tran1Balance;

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
        acct1Id = Long.valueOf(1);
        acct2Id = Long.valueOf(2);
        acct1 = FakeDataUtil.buildAcct(acct1Id);
        acct1Name = acct1.getName();
        acct1Fid = acct1.getFid();
        acctNbr1Id = Long.valueOf(3);
        acctNbr1 = FakeDataUtil.buildAcctNbr(acct1, acctNbr1Id);
        acctNbr1Number = acctNbr1.getNumber();
        tran1 = FakeDataUtil.buildTran(acct1, 4, null);
        tran1FitId = tran1.getFitId();
        tran1name = tran1.getName();
        tran1Balance = acct1.getBeginBalance().add(tran1.getAmount());
    }

    @Test
    public void findAllAccts() {
        Mockito.when(acctRepository.findAll()).thenReturn(new ArrayList<>());
        Iterable<Acct> accts = acctService.findAllAccts();
        assertFalse(accts.iterator().hasNext());
    }

    @Test
    public void findAcctById() throws Exception {
        Mockito.when(acctRepository.findById(acct1Id)).thenReturn(Optional.of(acct1));
        Acct acct = acctService.findAcctById(acct1Id);
        assertEquals(acct1Name, acct.getName());
    }

    @Test
    public void findAcctByIdNotFound() {
        Mockito.when(acctRepository.findById(acct2Id)).thenReturn(Optional.empty());
        try {
            Acct acct = acctService.findAcctById(acct2Id);
            fail();
        } catch (JunkNotFoundException e) {
        }
    }

    @Test
    public void saveAcct() throws Exception {
        Mockito.when(acctRepository.save(Mockito.any(Acct.class))).thenReturn(acct1);
        Acct acct = acctService.saveAcct(acct1);
        assertEquals(acct1Name, acct.getName());
    }

    @Test
    public void saveAcctOptimisticLocking() {
        Mockito.when(acctRepository.save(Mockito.any(Acct.class)))
                .thenThrow(ObjectOptimisticLockingFailureException.class);
        try {
            Acct acct = acctService.saveAcct(acct1);
            fail();
        } catch (JunkOptimisticLockingException e) {
        }
    }

    @Test
    public void findAcctsByFid() {
        List<Acct> accts1 = new ArrayList<>();
        accts1.add(acct1);
        Mockito.when(acctRepository.findByFid(acct1Fid)).thenReturn(accts1);
        List<Acct> accts = acctService.findAcctsByFid(acct1Fid);
        assertEquals(1, accts.size());
        assertEquals(acct1Name, accts.iterator().next().getName());
    }

    @Test
    public void findCurrentAcctNbrByAcctId() {
        List<AcctNbr> acctNbrList = new ArrayList<>();
        acctNbrList.add(acctNbr1);
        Mockito.when(acctNbrRepository.findTopByAcct_IdOrderByEffDateDesc(acct1Id)).thenReturn(acctNbrList);
        AcctNbr acctNbr = acctService.findCurrentAcctNbrByAcctId(acct1Id);
        assertEquals(acctNbr1Number, acctNbr.getNumber());
    }

    @Test
    public void findAcctNbrsByFidAndNumber() {
        List<AcctNbr> acctNbrList = new ArrayList<>();
        acctNbrList.add(acctNbr1);
        Mockito.when(acctNbrRepository.findByAcct_FidAndNumber(acct1Fid, acctNbr1Number)).thenReturn(acctNbrList);
        List<AcctNbr> acctNbrs = acctService.findAcctNbrsByFidAndNumber(acct1Fid, acctNbr1Number);
        assertEquals(1, acctNbrs.size());
        assertEquals(acctNbr1Id, acctNbrs.iterator().next().getId());
    }

    @Test
    public void findTransByAcctIdAndFitId() {
        List<Tran> tranList = new ArrayList<>();
        tranList.add(tran1);
        Mockito.when(tranRepository.findByAcct_IdAndFitId(acct1Id, tran1FitId)).thenReturn(tranList);
        List<Tran> trans = acctService.findTransByAcctIdAndFitId(acct1Id, tran1FitId);
        assertEquals(1, trans.size());
        assertEquals(tran1name, trans.iterator().next().getName());
    }

    @Test
    public void findTranBalancesByAcctId() throws Exception {
        Mockito.when(acctRepository.findById(acct1Id)).thenReturn(Optional.of(acct1));
        List<TranBalanceBean> beans = acctService.findTranBalancesByAcctId(acct1Id);
        assertEquals(1, beans.size());
        assertEquals(0, tran1Balance.compareTo(beans.iterator().next().getBalance()));
    }

    @Test
    public void findAllAcctSummaries() {
        List<Acct> acctsList = new ArrayList<>();
        acctsList.add(acct1);
        Mockito.when(acctRepository.findAll()).thenReturn(acctsList);
        List<AcctSummaryBean> beans = acctService.findAllAcctSummaries();
        assertEquals(1, beans.size());
        assertEquals(0, tran1Balance.compareTo(beans.iterator().next().getBalance()));
    }
}