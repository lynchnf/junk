package norman.junk.controller;

import java.util.Optional;
import javax.validation.Valid;
import norman.junk.domain.Payable;
import norman.junk.domain.Payee;
import norman.junk.service.PayableService;
import norman.junk.service.PayeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PayableController {
    private static final Logger logger = LoggerFactory.getLogger(PayableController.class);
    @Autowired
    private PayeeService payeeService;
    @Autowired
    private PayableService payableService;

    @RequestMapping("/payableList")
    public String loadList(Model model) {
        Iterable<Payable> payables = payableService.findAllPayables();
        model.addAttribute("payables", payables);
        return "payableList";
    }

    @RequestMapping("/payable")
    public String loadView(@RequestParam("payableId") Long payableId, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Payable> optionalPayable = payableService.findPayableById(payableId);
        // If no payable, we gots an error.
        if (!optionalPayable.isPresent()) {
            String errorMessage = "Payable not found, payableId=\"" + payableId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to view payable.
        Payable payable = optionalPayable.get();
        model.addAttribute("payable", payable);
        return "payableView";
    }

    @GetMapping("/payableEdit")
    public String loadEdit(@RequestParam(value = "payableId", required = false) Long payableId,
            @RequestParam(value = "payeeId", required = false) Long payeeId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no payable id, new payable.
        if (payableId == null) {
            PayableForm payableForm = new PayableForm();
            if (payeeId != null)
                payableForm.setPayeeId(payeeId);
            model.addAttribute("payableForm", payableForm);
            return "payableEdit";
        }
        Optional<Payable> optionalPayable = payableService.findPayableById(payableId);
        // If no payable, we gots an error.
        if (!optionalPayable.isPresent()) {
            String errorMessage = "Payable not found, payableId=\"" + payableId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to edit payable.
        Payable payable = optionalPayable.get();
        PayableForm payableForm = new PayableForm(payable);
        model.addAttribute("payableForm", payableForm);
        return "payableEdit";
    }

    @PostMapping("/payableEdit")
    public String processEdit(@Valid PayableForm payableForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "payableEdit";
        }
        Long payableId = payableForm.getId();
        Payable payable;
        if (payableId != null) {
            Optional<Payable> optionalPayable = payableService.findPayableById(payableId);
            // If no payable, we gots an error.
            if (!optionalPayable.isPresent()) {
                String errorMessage = "Payable not found, payableId=\"" + payableId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            // Prepare to savePayment existing payable.
            payable = payableForm.toPayable(payeeService);
        } else {
            // If no payable id, prepare to savePayment new payable.
            payable = payableForm.toPayable(payeeService);
        }
        // Try to savePayment payable.
        Payable save;
        try {
            save = payableService.savePayable(payable);
            String successMessage = "Payable successfully added, payableId=\"" + save.getId() + "\"";
            if (payableId != null)
                successMessage = "Payable successfully updated, payableId=\"" + save.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("payableId", save.getId());
        } catch (Exception e) {
            String errorMessage = "New payable could not be added";
            if (payableId != null)
                errorMessage = "Payable could not be updated, payableId=\"" + payableId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
            if (payableId == null) {
                return "redirect:/";
            } else {
                redirectAttributes.addAttribute("payableId", payableId);
                return "redirect:/payable?payableId={payableId}";
            }
        }
        return "redirect:/payable?payableId={payableId}";
    }

    @ModelAttribute("allPayees")
    public Iterable<Payee> loadPayeeDropDown() {
        return payeeService.findAllPayees();
    }
}