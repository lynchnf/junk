package norman.junk.controller;

import norman.junk.domain.DataFile;
import norman.junk.domain.Payee;
import norman.junk.service.AcctService;
import norman.junk.service.AcctSummaryBean;
import norman.junk.service.DataFileService;
import norman.junk.service.PayeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class DashboardController {
    @Autowired
    private AcctService acctService;
    @Autowired
    private DataFileService dataFileService;
    @Autowired
    private PayeeService payeeService;

    @RequestMapping("/")
    public String loadDashboard(Model model) {
        List<AcctSummaryBean> acctSummaries = acctService.findAllAcctSummaries();
        model.addAttribute("acctSummaries", acctSummaries);
        Iterable<DataFile> dataFiles = dataFileService.findAllDataFiles();
        model.addAttribute("dataFiles", dataFiles);
        Iterable<Payee> payees = payeeService.findAllPayees();
        model.addAttribute("payees", payees);
        return "index";
    }
}