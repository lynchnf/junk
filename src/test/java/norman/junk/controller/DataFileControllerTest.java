package norman.junk.controller;

import java.util.Random;
import norman.junk.domain.DataFile;
import norman.junk.service.DataFileService;
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
@WebMvcTest(DataFileController.class)
public class DataFileControllerTest {
    private final Random random = new Random();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DataFileService dataFileService;

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dataFileList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("dataFileList"));
    }

    @Test
    public void loadView() throws Exception {
        DataFile dataFile = buildExistingDataFile();
        BDDMockito.given(dataFileService.findDataFileById(dataFile.getId())).willReturn(dataFile);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dataFile").param("dataFileId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("dataFileView"));
        resultActions.andExpect(
                MockMvcResultMatchers.content().string(StringContains.containsString(dataFile.getOriginalFilename())));
    }

    private DataFile buildExistingDataFile() {
        Long dataFileId = Long.valueOf(1);
        String originalFilename = RandomStringUtils.randomAlphabetic(50);
        DataFile dataFile = new DataFile();
        dataFile.setId(dataFileId);
        dataFile.setOriginalFilename(originalFilename);
        return dataFile;
    }
}