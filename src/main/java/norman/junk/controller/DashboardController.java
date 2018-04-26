package norman.junk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import norman.junk.domain.Acct;
import norman.junk.domain.DataFile;
import norman.junk.repository.AcctRepository;
import norman.junk.repository.DataFileRepository;
import norman.junk.repository.TranRepository;

@Controller
public class DashboardController {
    @Autowired
    private AcctRepository acctRepository;
    @Autowired
    private DataFileRepository dataFileRepository;
    @Autowired
    private TranRepository tranRepository;

    @RequestMapping("/")
    public String loadDashboard(Model model) {
        Iterable<Acct> accts = acctRepository.findAll();
        model.addAttribute("accts", accts);
        Iterable<DataFile> dataFiles = dataFileRepository.findAll();
        model.addAttribute("dataFiles", dataFiles);
        return "index";
    }
}