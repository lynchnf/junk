package norman.junk.controller;

import java.util.List;
import norman.junk.service.AcctService;
import norman.junk.service.AcctSummaryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {
    @Autowired
    private AcctService acctService;

    @RequestMapping("/")
    public String loadView(Model model) {
        List<AcctSummaryBean> acctSummaries = acctService.findAllAcctSummaries();
        model.addAttribute("acctSummaries", acctSummaries);
        return "index";
    }
}