package norman.junk.controller;

import java.util.ArrayList;
import java.util.List;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
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
    private static final String DATABASE_ERROR = "Unexpected Database Error.";
    private static final String TRAN_NOT_FOUND = "Tran not found.";
    @Autowired
    private TranService tranService;
    @Autowired
    private PatternService patternService;

    @RequestMapping("/tran")
    public String loadView(@RequestParam("tranId") Long tranId, Model model, RedirectAttributes redirectAttributes) {
        Tran tran;
        try {
            tran = tranService.findTranById(tranId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", TRAN_NOT_FOUND);
            return "redirect:/";
        }
        model.addAttribute("tran", tran);
        return "tranView";
    }

    @RequestMapping("/tranAssign")
    public String assignCategories(RedirectAttributes redirectAttributes) {
        List<Pattern> patterns = null;
        try {
            patterns = patternService.findAllPatterns();
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        List<Tran> trans = null;
        try {
            trans = tranService.findAllNonAssigned();
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
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
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        String successMessage = "Categories successfully assigned to " + assignUs.size() + " transactions";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/";
    }
}