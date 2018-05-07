package norman.junk.controller;

import java.util.Optional;

import javax.validation.Valid;

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

import norman.junk.domain.PayAcct;
import norman.junk.domain.Payee;
import norman.junk.service.PayAcctService;
import norman.junk.service.PayeeService;

@Controller
public class PayAcctController {
    private static final Logger logger = LoggerFactory.getLogger(PayAcctController.class);
    @Autowired
    private PayeeService payeeService;
    @Autowired
    private PayAcctService payAcctService;

    @RequestMapping("/payAcct")
    public String loadView(@RequestParam("payAcctId") Long payAcctId, Model model, RedirectAttributes redirectAttributes) {
        Optional<PayAcct> optionalPayAcct = payAcctService.findPayAcctById(payAcctId);
        // If no payAcct, we gots an error.
        if (!optionalPayAcct.isPresent()) {
            String errorMessage = "PayAcct not found, payAcctId=\"" + payAcctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to view payAcct.
        PayAcct payAcct = optionalPayAcct.get();
        model.addAttribute("payAcct", payAcct);
        return "payAcctView";
    }

    @GetMapping("/payAcctEdit")
    public String loadEdit(@RequestParam(value = "payeeId", required = false) Long payeeId, @RequestParam(value = "payAcctId", required = false) Long payAcctId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no payAcct id, new payAcct. We must have a valid payeeId.
        if (payAcctId == null) {
            if (payeeId == null) {
                String errorMessage = "Missing required PayId";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            Optional<Payee> optionalPayee = payeeService.findPayeeById(payeeId);
            // If no payee, we gots an error.
            if (!optionalPayee.isPresent()) {
                String errorMessage = "Payee not found, payeeId=\"" + payeeId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            Payee payee = optionalPayee.get();
            PayAcctForm payAcctForm = new PayAcctForm();
            payAcctForm.setPayeeId(payee.getId());
            payAcctForm.setPayeeName(payee.getName());
            model.addAttribute("payAcctForm", payAcctForm);
            return "payAcctEdit";
        }
        Optional<PayAcct> optionalPayAcct = payAcctService.findPayAcctById(payAcctId);
        // If no payAcct, we gots an error.
        if (!optionalPayAcct.isPresent()) {
            String errorMessage = "PayAcct not found, payAcctId=\"" + payAcctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to edit payAcct.
        PayAcct payAcct = optionalPayAcct.get();
        PayAcctForm payAcctForm = new PayAcctForm(payAcct);
        model.addAttribute("payAcctForm", payAcctForm);
        return "payAcctEdit";
    }

    @PostMapping("/payAcctEdit")
    public String processEdit(@Valid PayAcctForm payAcctForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "payAcctEdit";
        }
        Long payeeId = payAcctForm.getPayeeId();
        Optional<Payee> optionalPayee = payeeService.findPayeeById(payeeId);
        // If no payee, we gots an error.
        if (!optionalPayee.isPresent()) {
            String errorMessage = "Payee not found, payeeId=\"" + payeeId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        Payee payee = optionalPayee.get();
        Long payAcctId = payAcctForm.getId();
        PayAcct payAcct;
        if (payAcctId != null) {
            Optional<PayAcct> optionalPayAcct = payAcctService.findPayAcctById(payAcctId);
            // If no payAcct, we gots an error.
            if (!optionalPayAcct.isPresent()) {
                String errorMessage = "PayAcct not found, payAcctId=\"" + payAcctId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            // Prepare to save existing payAcct.
            payAcct = payAcctForm.toPayAcct();
        } else {
            // If no payAcct id, prepare to save new payAcct.
            payAcct = payAcctForm.toPayAcct();
        }
        // Attach payAcct to payee.
        payAcct.setPayee(payee);
        // Try to save payAcct.
        PayAcct save;
        try {
            save = payAcctService.savePayAcct(payAcct);
            String successMessage = "PayAcct successfully added, payAcctId=\"" + save.getId() + "\"";
            if (payAcctId != null) successMessage = "PayAcct successfully updated, payAcctId=\"" + save.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("payAcctId", save.getId());
        } catch (Exception e) {
            String errorMessage = "New payAcct could not be added";
            if (payAcctId != null) errorMessage = "PayAcct could not be updated, payAcctId=\"" + payAcctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
            if (payAcctId == null) {
                return "redirect:/";
            } else {
                redirectAttributes.addAttribute("payAcctId", payAcctId);
                return "redirect:/payAcct?payAcctId={payAcctId}";
            }
        }
        return "redirect:/payAcct?payAcctId={payAcctId}";
    }
}