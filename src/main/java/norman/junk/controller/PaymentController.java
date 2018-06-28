package norman.junk.controller;

import javax.validation.Valid;
import norman.junk.DatabaseException;
import norman.junk.NewNotFoundException;
import norman.junk.NotFoundException;
import norman.junk.domain.Payable;
import norman.junk.domain.Payment;
import norman.junk.service.PayableService;
import norman.junk.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
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

import static norman.junk.controller.MessagesConstants.NOT_FOUND_ERROR;

@Controller
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private static final String DATABASE_ERROR = "Unexpected Database Error.";
    private static final String PAYMENT_NOT_FOUND = "Payment not found.";
    private static final String PAYABLE_NOT_FOUND = "Payable not found.";
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PayableService payableService;

    @RequestMapping("/paymentList")
    public String loadList(Model model, RedirectAttributes redirectAttributes) {
        Iterable<Payment> payments;
        try {
            payments = paymentService.findAllPayments();
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        model.addAttribute("payments", payments);
        return "paymentList";
    }

    @RequestMapping("/payment")
    public String loadView(@RequestParam("paymentId") Long paymentId, Model model,
            RedirectAttributes redirectAttributes) {
        Payment payment;
        try {
            payment = paymentService.findPaymentById(paymentId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", PAYMENT_NOT_FOUND);
            return "redirect:/";
        }
        model.addAttribute("payment", payment);
        return "paymentView";
    }

    @GetMapping("/paymentEdit")
    public String loadEdit(@RequestParam(value = "paymentId", required = false) Long paymentId,
            @RequestParam(value = "payableId", required = false) Long payableId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no payment id, new payment.
        if (paymentId == null) {
            // Must have a payable.
            if (payableId == null) {
                String errorMessage = "No payable id.";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            Payable payable = null;
            try {
                payable = payableService.findPayableById(payableId);
            } catch (NewNotFoundException e) {
                redirectAttributes
                        .addFlashAttribute("errorMessage", String.format(NOT_FOUND_ERROR, "Payable", payableId));
                return "redirect:/payableList";
            }
            PaymentForm paymentForm = new PaymentForm();
            paymentForm.setPayableId(payableId);
            if (StringUtils.isBlank(payable.getPayee().getNickname())) {
                paymentForm.setPayeeDisplayName(payable.getPayee().getName());
            } else {
                paymentForm.setPayeeDisplayName(payable.getPayee().getNickname());
            }
            paymentForm.setPayableDueDate(payable.getDueDate());
            paymentForm.setPayableAmountDue(payable.getAmountDue());
            model.addAttribute("paymentForm", paymentForm);
            return "paymentEdit";
        }
        // Otherwise, edit existing payment.
        Payment payment;
        try {
            payment = paymentService.findPaymentById(paymentId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", PAYMENT_NOT_FOUND);
            return "redirect:/";
        }
        PaymentForm paymentForm = new PaymentForm(payment);
        model.addAttribute("paymentForm", paymentForm);
        return "paymentEdit";
    }

    @PostMapping("/paymentEdit")
    public String processEdit(@Valid PaymentForm paymentForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "paymentEdit";
        }
        Long paymentId = paymentForm.getId();
        Payment payment;
        // Verify existing payment still exists.
        if (paymentId != null) {
            try {
                paymentService.findPaymentById(paymentId);
            } catch (DatabaseException e) {
                redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
                return "redirect:/";
            } catch (NotFoundException e) {
                redirectAttributes.addFlashAttribute("errorMessage", PAYMENT_NOT_FOUND);
                return "redirect:/";
            }
        }
        // Convert form to entity ...
        payment = paymentForm.toPayment();
        // ... attach parent entity ...
        Long payableId = paymentForm.getPayableId();
        try {
            Payable payable = payableService.findPayableById(payableId);
        } catch (NewNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", String.format(NOT_FOUND_ERROR, "Payable", payableId));
            return "redirect:/payableList";
        }
        // .. and save.
        Payment save;
        try {
            save = paymentService.savePayment(payment);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        String successMessage = "Payment successfully added, paymentId=\"" + save.getId() + "\"";
        if (paymentId != null)
            successMessage = "Payment successfully updated, paymentId=\"" + save.getId() + "\"";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        redirectAttributes.addAttribute("paymentId", save.getId());
        return "redirect:/payment?paymentId={paymentId}";
    }
}