package norman.junk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DataFileController {
    private static final Logger logger = LoggerFactory.getLogger(DataFileController.class);
    @Autowired
    private DataFileRepository dataFileRepository;

    @PostMapping("/dataFileUpload")
    public String processUpload(@RequestParam(value = "multipartFile") MultipartFile multipartFile, RedirectAttributes redirectAttributes) {
        try {
            DataFile dataFile = saveUploadedFile(multipartFile);
            OfxParseResponse response = OfxParseUtils.parseUploadedFile(dataFile);
            String successMessage = "Data file successfully uploaded, dataFileId=\"" + dataFile.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (JunkException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/";
    }

    @RequestMapping("/dataFile")
    public String loadView(@RequestParam("dataFileId") Long dataFileId, Model model, RedirectAttributes redirectAttributes) {
        Optional<DataFile> optional = dataFileRepository.findById(dataFileId);
        if (optional.isPresent()) {
            model.addAttribute("dataFile", optional.get());
            return "dataFileView";
        } else {
            String errorMessage = "Account not found, dataFileId=\"" + dataFileId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.warn(errorMessage);
            return "redirect:/";
        }
    }

    private DataFile saveUploadedFile(MultipartFile multipartFile) throws JunkException {
        DataFile dataFile = new DataFile();
        dataFile.setOriginalFilename(multipartFile.getOriginalFilename());
        dataFile.setContentType(multipartFile.getContentType());
        dataFile.setEmpty(multipartFile.isEmpty());
        dataFile.setSize(multipartFile.getSize());
        dataFile.setUploadTimestamp(new Date());
        dataFile = readLines(multipartFile, dataFile);
        return dataFileRepository.save(dataFile);
    }

    private DataFile readLines(MultipartFile multipartFile, DataFile dataFile) throws JunkException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
            String line;
            int seq = 0;
            while ((line = reader.readLine()) != null) {
                DataLine dataLine = new DataLine();
                dataLine.setDataFile(dataFile);
                dataLine.setSeq(seq++);
                dataLine.setText(line);
                dataFile.getDataLines().add(dataLine);
            }
            return dataFile;
        } catch (IOException e) {
            String msg = "Error while reading from uploaded file " + multipartFile.getOriginalFilename() + ".";
            logger.error(msg, e);
            throw new JunkException(msg, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    String msg = "Error ignored while closing input stream.";
                    logger.warn(msg, e);
                }
            }
        }
    }
}