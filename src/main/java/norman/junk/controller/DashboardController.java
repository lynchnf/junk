package norman.junk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import norman.junk.domain.Acct;
import norman.junk.domain.DataFile;
import norman.junk.service.AcctService;
import norman.junk.service.DataFileService;

@Controller
public class DashboardController {
    @Autowired
    private AcctService acctService;
    @Autowired
    private DataFileService dataFileService;

    @RequestMapping("/")
    public String loadDashboard(Model model) {
        Iterable<Acct> accts = acctService.findAllAccts();
        model.addAttribute("accts", accts);
        Iterable<DataFile> dataFiles = dataFileService.findAllDataFiles();
        model.addAttribute("dataFiles", dataFiles);
        return "index";
    }
}