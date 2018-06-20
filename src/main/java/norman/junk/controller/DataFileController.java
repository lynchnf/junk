package norman.junk.controller;

import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
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
    private static final String DATABASE_ERROR = "Unexpected Database Error.";
    private static final String DATA_FILE_NOT_FOUND = "DataFile not found.";
    @Autowired
    private DataFileService dataFileService;

    @RequestMapping("/dataFileList")
    public String loadList(Model model, RedirectAttributes redirectAttributes) {
        Iterable<DataFile> dataFiles;
        try {
            dataFiles = dataFileService.findAllDataFiles();
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        model.addAttribute("dataFiles", dataFiles);
        return "dataFileList";
    }

    @RequestMapping("/dataFile")
    public String loadView(@RequestParam("dataFileId") Long dataFileId, Model model,
            RedirectAttributes redirectAttributes) {
        DataFile dataFile;
        try {
            dataFile = dataFileService.findDataFileById(dataFileId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATA_FILE_NOT_FOUND);
            return "redirect:/";
        }
        model.addAttribute("dataFile", dataFile);
        return "dataFileView";
    }
}