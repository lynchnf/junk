package norman.junk.controller;

import javax.validation.Valid;
import norman.junk.NewNotFoundException;
import norman.junk.NewOptimisticLockingException;
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
import static norman.junk.controller.MessagesConstants.OPTIMISTIC_LOCK_ERROR;
import static norman.junk.controller.MessagesConstants.PARENT_ID_NEEDED_FOR_ADD_ERROR;
import static norman.junk.controller.MessagesConstants.SUCCESSFULLY_ADDED;
import static norman.junk.controller.MessagesConstants.SUCCESSFULLY_UPDATED;

@Controller
public class PaymentController {
    // FIXME REFACTOR
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PayableService payableService;

    @RequestMapping("/paymentList")
    public String loadList(Model model) {
        Iterable<Payment> payments = paymentService.findAllPayments();
        model.addAttribute("payments", payments);
        return "paymentList";
    }

    @RequestMapping("/payment")
    public String loadView(@RequestParam("paymentId") Long paymentId, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Payment payment = paymentService.findPaymentById(paymentId);
            model.addAttribute("payment", payment);
            return "paymentView";
        } catch (NewNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", String.format(NOT_FOUND_ERROR, "Payment", paymentId));
            return "redirect:/paymentList";
        }
    }

    @GetMapping("/paymentEdit")
    public String loadEdit(@RequestParam(value = "paymentId", required = false) Long paymentId,
            @RequestParam(value = "payableId", required = false) Long payableId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no payment id, new payment.
        if (paymentId == null) {
            if (payableId == null) {
                String msg = String.format(PARENT_ID_NEEDED_FOR_ADD_ERROR, "Payment", "Payable");
                logger.warn(msg);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/payableList";
            }
            try {
                Payable payable = payableService.findPayableById(payableId);
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
            } catch (NewNotFoundException e) {
                redirectAttributes
                        .addFlashAttribute("errorMessage", String.format(NOT_FOUND_ERROR, "Payable", payableId));
                return "redirect:/payableList";
            }
        }
        // Otherwise, edit existing payment.
        try {
            Payment payment = paymentService.findPaymentById(paymentId);
            PaymentForm paymentForm = new PaymentForm(payment);
            model.addAttribute("paymentForm", paymentForm);
            return "paymentEdit";
        } catch (NewNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", String.format(NOT_FOUND_ERROR, "Payment", paymentId));
            return "redirect:/paymentList";
        }
    }

    @PostMapping("/paymentEdit")
    public String processEdit(@Valid PaymentForm paymentForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "paymentEdit";
        }
        // Convert form to entity and save.
        Long paymentId = paymentForm.getId();
        Payment payment = paymentForm.toPayment(payableService);
        try {
            Payment save = paymentService.savePayment(payment);
            String successMessage = String.format(SUCCESSFULLY_ADDED, "Payment", save.getId());
            if (paymentId != null)
                successMessage = String.format(SUCCESSFULLY_UPDATED, "Payment", save.getId());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("paymentId", save.getId());
            return "redirect:/payment?paymentId={paymentId}";
        } catch (NewOptimisticLockingException e) {
            redirectAttributes
                    .addFlashAttribute("errorMessage", String.format(OPTIMISTIC_LOCK_ERROR, "Payment", paymentId));
            redirectAttributes.addAttribute("paymentId", paymentId);
            return "redirect:/payment?paymentId={paymentId}";
        }
    }
}