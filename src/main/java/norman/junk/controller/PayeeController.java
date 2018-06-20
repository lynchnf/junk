package norman.junk.controller;

import javax.validation.Valid;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
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
    private static final String DATABASE_ERROR = "Unexpected Database Error.";
    private static final String PAYEE_NOT_FOUND = "Payee not found.";
    @Autowired
    private PayeeService payeeService;

    @RequestMapping("/payeeList")
    public String loadList(Model model, RedirectAttributes redirectAttributes) {
        Iterable<Payee> payees;
        try {
            payees = payeeService.findAllPayees();
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        model.addAttribute("payees", payees);
        return "payeeList";
    }

    @RequestMapping("/payee")
    public String loadView(@RequestParam("payeeId") Long payeeId, Model model, RedirectAttributes redirectAttributes) {
        Payee payee;
        try {
            payee = payeeService.findPayeeById(payeeId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", PAYEE_NOT_FOUND);
            return "redirect:/";
        }
        model.addAttribute("payee", payee);
        return "payeeView";
    }

    @GetMapping("/payeeEdit")
    public String loadEdit(@RequestParam(value = "payeeId", required = false) Long payeeId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no payee id, new payee.
        if (payeeId == null) {
            model.addAttribute("payeeForm", new PayeeForm());
            return "payeeEdit";
        }
        // Otherwise, edit existing payee.
        Payee payee;
        try {
            payee = payeeService.findPayeeById(payeeId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", PAYEE_NOT_FOUND);
            return "redirect:/";
        }
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
        // Verify existing payee still exists.
        if (payeeId != null) {
            try {
                payeeService.findPayeeById(payeeId);
            } catch (DatabaseException e) {
                redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
                return "redirect:/";
            } catch (NotFoundException e) {
                redirectAttributes.addFlashAttribute("errorMessage", PAYEE_NOT_FOUND);
                return "redirect:/";
            }
        }
        // Convert form to entity and save.
        payee = payeeForm.toPayee();
        Payee save;
        try {
            save = payeeService.savePayee(payee);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        String successMessage = "Payee successfully added, payeeId=\"" + save.getId() + "\"";
        if (payeeId != null)
            successMessage = "Payee successfully updated, payeeId=\"" + save.getId() + "\"";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        redirectAttributes.addAttribute("payeeId", save.getId());
        return "redirect:/payee?payeeId={payeeId}";
    }
}