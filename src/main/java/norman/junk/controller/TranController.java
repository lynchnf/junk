package norman.junk.controller;

import norman.junk.domain.Tran;
import norman.junk.repository.TranRepository;
import norman.junk.service.TranService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class TranController {
    private static final Logger logger = LoggerFactory.getLogger(TranController.class);
    @Autowired
    private TranService tranService;

    @RequestMapping("/tran")
    public String loadView(@RequestParam("tranId") Long tranId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Tran> optionalTran = tranService.findById(tranId);
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
}