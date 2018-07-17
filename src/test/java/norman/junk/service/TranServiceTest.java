package norman.junk.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import norman.junk.FakeDataUtil;
import norman.junk.JunkNotFoundException;
import norman.junk.JunkOptimisticLockingException;
import norman.junk.domain.Acct;
import norman.junk.domain.Tran;
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
public class TranServiceTest {
    private Long tran1Id;
    private Long tran2Id;
    private Acct acct1;
    private Tran tran1;
    private String tran1Name;

    @TestConfiguration
    static class TranServiceTestConfiguration {
        @Bean
        public TranService tranService() {
            return new TranService();
        }
    }

    @Autowired
    private TranService tranService;
    @MockBean
    private TranRepository tranRepository;

    @Before
    public void setUp() throws Exception {
        tran1Id = Long.valueOf(1);
        tran2Id = Long.valueOf(2);
        acct1 = FakeDataUtil.buildAcct(3);
        tran1 = FakeDataUtil.buildTran(acct1, tran1Id, null);
        tran1Name = tran1.getName();
    }

    @Test
    public void findTranById() throws Exception {
        Mockito.when(tranRepository.findById(tran1Id)).thenReturn(Optional.of(tran1));
        Tran tran = tranService.findTranById(tran1Id);
        assertEquals(tran1Name, tran.getName());
    }

    @Test
    public void findTranByIdNotFound() {
        Mockito.when(tranRepository.findById(tran2Id)).thenReturn(Optional.empty());
        try {
            Tran tran = tranService.findTranById(tran2Id);
            fail();
        } catch (JunkNotFoundException e) {
        }
    }

    @Test
    public void saveTran() throws Exception {
        List<Tran> trans1 = new ArrayList<>();
        trans1.add(tran1);
        Mockito.when(tranRepository.saveAll(Mockito.anyIterable())).thenReturn(trans1);
        Iterable<Tran> trans2 = tranService.saveAllTrans(trans1);
        Iterator<Tran> trans2Iterator = trans2.iterator();
        assertEquals(tran1Name, trans2Iterator.next().getName());
        assertFalse(trans2Iterator.hasNext());
    }

    @Test
    public void saveTranOptimisticLocking() {
        List<Tran> trans1 = new ArrayList<>();
        trans1.add(tran1);
        Mockito.when(tranRepository.saveAll(Mockito.anyIterable()))
                .thenThrow(ObjectOptimisticLockingFailureException.class);
        try {
            Iterable<Tran> trans2 = tranService.saveAllTrans(trans1);
            fail();
        } catch (JunkOptimisticLockingException e) {
        }
    }

    @Test
    public void findAllNonAssigned() {
        Mockito.when(tranRepository.findAll()).thenReturn(new ArrayList<>());
        Iterable<Tran> trans = tranService.findAllNonAssigned();
        assertFalse(trans.iterator().hasNext());
    }
}