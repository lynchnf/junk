package norman.junk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {
    @Autowired
    private AcctRepository acctRepository;
    @Autowired
    private DataFileRepository dataFileRepository;

    @RequestMapping("/")
    public String loadDashboard(Model model) {
        Iterable<Acct> accts = acctRepository.findAll();
        model.addAttribute("accts", accts);
        Iterable<DataFile> dataFiles = dataFileRepository.findAll();
        model.addAttribute("dataFiles", dataFiles);
        return "index";
    }
}