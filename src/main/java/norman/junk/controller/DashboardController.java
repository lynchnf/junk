package norman.junk.controller;

import java.util.List;
import norman.junk.service.AcctService;
import norman.junk.service.AcctSummaryBean;
import norman.junk.service.PayableDueBean;
import norman.junk.service.PayableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {
    // FIXME REFACTOR
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    @Autowired
    private AcctService acctService;
    @Autowired
    private PayableService payableService;

    @RequestMapping("/")
    public String loadView(Model model) {
        List<AcctSummaryBean> acctSummaries = acctService.findAllAcctSummaries();
        model.addAttribute("acctSummaries", acctSummaries);
        List<PayableDueBean> payableDues = payableService.findAllPayableDues();
        model.addAttribute("payableDues", payableDues);
        return "index";
    }
}