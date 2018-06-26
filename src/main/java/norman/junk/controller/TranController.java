package norman.junk.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

@Controller
public class TranController {
    private static final Logger logger = LoggerFactory.getLogger(TranController.class);
    @Autowired
    private TranService tranService;
    @Autowired
    private PatternService patternService;

    @RequestMapping("/tran")
    public String loadView(@RequestParam("tranId") Long tranId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Tran> optionalTran = tranService.findTranById(tranId);
        // If no tran, we gots an error.
        if (!optionalTran.isPresent()) {
            String errorMessage = "Tran not found, tranId=\"" + tranId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to view tran.
        Tran tran = optionalTran.get();
        model.addAttribute("tran", tran);
        return "tranView";
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
        } catch (Exception e) {
            String errorMessage = "Categories could not be assigned to transactions";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
        }
        return "redirect:/";
    }
}