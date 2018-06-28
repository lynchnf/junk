package norman.junk.controller;

import javax.validation.Valid;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
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
    private static final String DATABASE_ERROR = "Unexpected Database Error.";
    private static final String PAYEE_NOT_FOUND = "Payee not found.";
    private static final String PAYABLE_NOT_FOUND = "Payable not found";
    @Autowired
    private PayeeService payeeService;
    @Autowired
    private PayableService payableService;

    @RequestMapping("/payableList")
    public String loadList(Model model, RedirectAttributes redirectAttributes) {
        Iterable<Payable> payables;
        try {
            payables = payableService.findAllPayables();
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        model.addAttribute("payables", payables);
        return "payableList";
    }

    @RequestMapping("/payable")
    public String loadView(@RequestParam("payableId") Long payableId, Model model,
            RedirectAttributes redirectAttributes) {
        Payable payable;
        try {
            payable = payableService.findPayableById(payableId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", PAYABLE_NOT_FOUND);
            return "redirect:/";
        }
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
        // Otherwise, edit existing payable.
        Payable payable;
        try {
            payable = payableService.findPayableById(payableId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", PAYABLE_NOT_FOUND);
            return "redirect:/";
        }
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
        // Verify existing payable still exists.
        if (payableId != null) {
            try {
                payable = payableService.findPayableById(payableId);
            } catch (DatabaseException e) {
                redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
                return "redirect:/";
            } catch (NotFoundException e) {
                redirectAttributes.addFlashAttribute("errorMessage", PAYABLE_NOT_FOUND);
                return "redirect:/";
            }
        }
        // Convert form to entity ...
        payable = payableForm.toPayable(payeeService);
        // ... and save.
        Payable save;
        try {
            save = payableService.savePayable(payable);
        } catch (DatabaseException e) {
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
        String successMessage = "Payable successfully added, payableId=\"" + save.getId() + "\"";
        if (payableId != null)
            successMessage = "Payable successfully updated, payableId=\"" + save.getId() + "\"";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        redirectAttributes.addAttribute("payableId", save.getId());
        return "redirect:/payable?payableId={payableId}";
    }

    @ModelAttribute("allPayees")
    public Iterable<Payee> loadPayeeDropDown() throws DatabaseException {
        // FIXME Handle exception somehow.
        return payeeService.findAllPayees();
    }
}