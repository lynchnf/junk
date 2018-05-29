package norman.junk.controller;

import java.util.Optional;
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
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DataFileService dataFileService;

    @Test
    public void loadView() throws Exception {
        Long dataFileId = Long.valueOf(1);
        DataFile dataFile = new DataFile();
        dataFile.setId(dataFileId);
        String originalFilename = RandomStringUtils.randomAlphabetic(50);
        dataFile.setOriginalFilename(originalFilename);
        Optional<DataFile> optionalDataFile = Optional.of(dataFile);
        BDDMockito.given(dataFileService.findDataFileById(dataFileId)).willReturn(optionalDataFile);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dataFile").param("dataFileId", "1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("dataFileView"));
        resultActions.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString(originalFilename)));
    }

    @Test
    public void loadViewDataFileNotExist() throws Exception {
        Long dataFileId = Long.valueOf(2);
        Optional<DataFile> optionalDataFile = Optional.empty();
        BDDMockito.given(dataFileService.findDataFileById(dataFileId)).willReturn(optionalDataFile);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dataFile").param("dataFileId", "2");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isFound());
        resultActions.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
        resultActions.andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"));
    }

    @Test
    public void loadList() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dataFileList");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.view().name("dataFileList"));
    }
}