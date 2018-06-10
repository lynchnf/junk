package norman.junk.controller;

import java.util.Optional;
import norman.junk.domain.DataFile;
import norman.junk.service.DataFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DataFileController {
    private static final Logger logger = LoggerFactory.getLogger(DataFileController.class);
    @Autowired
    private DataFileService dataFileService;

    @RequestMapping("/dataFileList")
    public String loadList(Model model) {
        Iterable<DataFile> dataFiles = dataFileService.findAllDataFiles();
        model.addAttribute("dataFiles", dataFiles);
        return "dataFileList";
    }

    @RequestMapping("/dataFile")
    public String loadView(@RequestParam("dataFileId") Long dataFileId, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<DataFile> optional = dataFileService.findDataFileById(dataFileId);
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
}