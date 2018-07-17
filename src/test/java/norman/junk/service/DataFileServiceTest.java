package norman.junk.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import norman.junk.FakeDataUtil;
import norman.junk.JunkNotFoundException;
import norman.junk.JunkOptimisticLockingException;
import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.DataFile;
import norman.junk.domain.Tran;
import norman.junk.repository.DataFileRepository;
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
public class DataFileServiceTest {
    private Long dataFile1Id;
    private Long dataFile2Id;
    private DataFile dataFile1;
    private Date dataFile1UploadTimestamp;

    @TestConfiguration
    static class DataFileServiceTestConfiguration {
        @Bean
        public DataFileService dataFileService() {
            return new DataFileService();
        }
    }

    @Autowired
    private DataFileService dataFileService;
    @MockBean
    private DataFileRepository dataFileRepository;

    @Before
    public void setUp() throws Exception {
        dataFile1Id = Long.valueOf(1);
        dataFile2Id = Long.valueOf(2);
        Acct acct = FakeDataUtil.buildAcct(3);
        AcctNbr acctNbr = FakeDataUtil.buildAcctNbr(acct, 4);
        Tran tran = FakeDataUtil.buildTran(acct, 5, null);
        List<String> ofxFile = FakeDataUtil.buildOfxFile(acct, tran);
        dataFile1 = FakeDataUtil.buildDataFile(ofxFile, dataFile1Id, 6);
        dataFile1UploadTimestamp = dataFile1.getUploadTimestamp();
    }

    @Test
    public void findAllDataFiles() {
        Mockito.when(dataFileRepository.findAll()).thenReturn(new ArrayList<>());
        Iterable<DataFile> dataFiles = dataFileService.findAllDataFiles();
        assertFalse(dataFiles.iterator().hasNext());
    }

    @Test
    public void findDataFileById() throws Exception {
        Mockito.when(dataFileRepository.findById(dataFile1Id)).thenReturn(Optional.of(dataFile1));
        DataFile dataFile = dataFileService.findDataFileById(dataFile1Id);
        assertEquals(dataFile1UploadTimestamp, dataFile.getUploadTimestamp());
    }

    @Test
    public void findDataFileByIdNotFound() {
        Mockito.when(dataFileRepository.findById(dataFile2Id)).thenReturn(Optional.empty());
        try {
            DataFile dataFile = dataFileService.findDataFileById(dataFile2Id);
            fail();
        } catch (JunkNotFoundException e) {
        }
    }

    @Test
    public void saveDataFile() throws Exception {
        Mockito.when(dataFileRepository.save(Mockito.any(DataFile.class))).thenReturn(dataFile1);
        DataFile dataFile = dataFileService.saveDataFile(dataFile1);
        assertEquals(dataFile1UploadTimestamp, dataFile.getUploadTimestamp());
    }

    @Test
    public void saveDataFileOptimisticLocking() {
        Mockito.when(dataFileRepository.save(Mockito.any(DataFile.class)))
                .thenThrow(ObjectOptimisticLockingFailureException.class);
        try {
            DataFile dataFile = dataFileService.saveDataFile(dataFile1);
            fail();
        } catch (JunkOptimisticLockingException e) {
        }
    }
}