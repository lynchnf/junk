package norman.junk.controller;

import java.util.List;
import norman.junk.service.AcctService;
import norman.junk.service.AcctSummaryBean;
import norman.junk.service.PayableBalanceBean;
import norman.junk.service.PayableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {
    @Autowired
    private AcctService acctService;
    @Autowired
    private PayableService payableService;

    @RequestMapping("/")
    public String loadView(Model model) {
        List<AcctSummaryBean> acctSummaries = acctService.findAllAcctSummaries();
        model.addAttribute("acctSummaries", acctSummaries);
        List<PayableBalanceBean> payableBalances = payableService.findPayableBalancesDue();
        model.addAttribute("payableBalances", payableBalances);
        return "index";
    }
}