package norman.junk.controller;

import java.util.ArrayList;
import java.util.List;
import norman.junk.JunkInconceivableException;
import norman.junk.JunkNotFoundException;
import norman.junk.JunkOptimisticLockingException;
import norman.junk.domain.Pattern;
import norman.junk.domain.Tran;
import norman.junk.service.PatternService;
import norman.junk.service.TranService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static norman.junk.controller.MessagesConstants.NOT_FOUND_ERROR;
import static norman.junk.controller.MessagesConstants.UNEXPECTED_ERROR;

@Controller
public class TranController {
    // FIXME REFACTOR
    private static final Logger logger = LoggerFactory.getLogger(TranController.class);
    @Autowired
    private TranService tranService;
    @Autowired
    private PatternService patternService;

    @RequestMapping("/tran")
    public String loadView(@RequestParam("tranId") Long tranId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Tran tran = tranService.findTranById(tranId);
            model.addAttribute("tran", tran);
            return "tranView";
        } catch (JunkNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", String.format(NOT_FOUND_ERROR, "Transaction", tranId));
            return "redirect:/";
        }
    }

    @RequestMapping("/tranAssign")
    public String assignCategories(RedirectAttributes redirectAttributes) {
        List<Pattern> patterns = patternService.findAllPatterns();
        List<Tran> trans = tranService.findAllNonAssigned();
        List<Tran> assignUs = new ArrayList<>();
        for (Tran tran : trans) {
            boolean found = false;
            for (int i = 0; i < patterns.size() && !found; i++) {
                Pattern pattern = patterns.get(i);
                if (tran.getName().matches(pattern.getTranName())) {
                    tran.setCategory(pattern.getCategory());
                    assignUs.add(tran);
                    found = true;
                }
            }
        }
        try {
            Iterable<Tran> saveAll = tranService.saveAllTrans(assignUs);
            String successMessage = "Categories successfully assigned to " + assignUs.size() + " transactions";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/";
        } catch (JunkOptimisticLockingException e) {
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + ": " + e.getMessage());
        }
    }
}