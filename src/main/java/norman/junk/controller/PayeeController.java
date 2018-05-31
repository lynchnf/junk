package norman.junk.controller;

import java.util.Optional;
import javax.validation.Valid;
import norman.junk.domain.Payee;
import norman.junk.service.PayeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PayeeController {
    private static final Logger logger = LoggerFactory.getLogger(PayeeController.class);
    @Autowired
    private PayeeService payeeService;

    @RequestMapping("/payee")
    public String loadView(@RequestParam("payeeId") Long payeeId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Payee> optionalPayee = payeeService.findPayeeById(payeeId);
        // If no payee, we gots an error.
        if (!optionalPayee.isPresent()) {
            String errorMessage = "Payee not found, payeeId=\"" + payeeId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to view payee.
        Payee payee = optionalPayee.get();
        model.addAttribute("payee", payee);
        return "payeeView";
    }

    @RequestMapping("/payeeList")
    public String loadList(Model model) {
        Iterable<Payee> payees = payeeService.findAllPayees();
        model.addAttribute("payees", payees);
        return "payeeList";
    }

    @GetMapping("/payeeEdit")
    public String loadEdit(@RequestParam(value = "payeeId", required = false) Long payeeId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no payee id, new payee.
        if (payeeId == null) {
            model.addAttribute("payeeForm", new PayeeForm());
            return "payeeEdit";
        }
        Optional<Payee> optionalPayee = payeeService.findPayeeById(payeeId);
        // If no payee, we gots an error.
        if (!optionalPayee.isPresent()) {
            String errorMessage = "Payee not found, payeeId=\"" + payeeId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to edit payee.
        Payee payee = optionalPayee.get();
        PayeeForm payeeForm = new PayeeForm(payee);
        model.addAttribute("payeeForm", payeeForm);
        return "payeeEdit";
    }

    @PostMapping("/payeeEdit")
    public String processEdit(@Valid PayeeForm payeeForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "payeeEdit";
        }
        Long payeeId = payeeForm.getId();
        Payee payee;
        if (payeeId != null) {
            Optional<Payee> optionalPayee = payeeService.findPayeeById(payeeId);
            // If no payee, we gots an error.
            if (!optionalPayee.isPresent()) {
                String errorMessage = "Payee not found, payeeId=\"" + payeeId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            // Prepare to savePayment existing payee.
            payee = payeeForm.toPayee();
        } else {
            // If no payee id, prepare to savePayment new payee.
            payee = payeeForm.toPayee();
        }
        // Try to savePayment payee.
        Payee save;
        try {
            save = payeeService.savePayee(payee);
            String successMessage = "Payee successfully added, payeeId=\"" + save.getId() + "\"";
            if (payeeId != null)
                successMessage = "Payee successfully updated, payeeId=\"" + save.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("payeeId", save.getId());
        } catch (Exception e) {
            String errorMessage = "New payee could not be added";
            if (payeeId != null)
                errorMessage = "Payee could not be updated, payeeId=\"" + payeeId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
            if (payeeId == null) {
                return "redirect:/";
            } else {
                redirectAttributes.addAttribute("payeeId", payeeId);
                return "redirect:/payee?payeeId={payeeId}";
            }
        }
        return "redirect:/payee?payeeId={payeeId}";
    }
}