package norman.junk.controller;

import java.util.Optional;
import javax.validation.Valid;
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

@Controller
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private PayableService payableService;
    @Autowired
    private PaymentService paymentService;

    @RequestMapping("/payment")
    public String loadView(@RequestParam("paymentId") Long paymentId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Payment> optionalPayment = paymentService.findPaymentById(paymentId);
        // If no payment, we gots an error.
        if (!optionalPayment.isPresent()) {
            String errorMessage = "Payment not found, paymentId=\"" + paymentId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to view payment.
        Payment payment = optionalPayment.get();
        model.addAttribute("payment", payment);
        return "paymentView";
    }

    @RequestMapping("/paymentList")
    public String loadList(Model model) {
        Iterable<Payment> payments = paymentService.findAllPayments();
        model.addAttribute("payments", payments);
        return "paymentList";
    }

    @GetMapping("/paymentEdit")
    public String loadEdit(@RequestParam(value = "paymentId", required = false) Long paymentId, @RequestParam(value = "payableId", required = false) Long payableId, Model model, RedirectAttributes redirectAttributes) {
        // If no payment id, new payment.
        if (paymentId == null) {
            // If no payable id, we gots an error.
            if (payableId == null) {
                String errorMessage = "No payable id.";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            Optional<Payable> optionalPayable = payableService.findPayableById(payableId);
            // If no payable, we gots an error.
            if (!optionalPayable.isPresent()) {
                String errorMessage = "Payable not found, payableId=\"" + payableId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            PaymentForm paymentForm = new PaymentForm();
            paymentForm.setPayableId(payableId);
            Payable payable = optionalPayable.get();
            if (StringUtils.isBlank(payable.getPayee().getNickname())) {
                paymentForm.setPayeeDisplayName(payable.getPayee().getName());
            } else {
                paymentForm.setPayeeDisplayName(payable.getPayee().getNickname());
            }
            paymentForm.setPayablePaymentDueDate(payable.getPaymentDueDate());
            paymentForm.setPayableNewBalanceTotal(payable.getNewBalanceTotal());
            model.addAttribute("paymentForm", paymentForm);
            return "paymentEdit";
        }
        Optional<Payment> optionalPayment = paymentService.findPaymentById(paymentId);
        // If no payment, we gots an error.
        if (!optionalPayment.isPresent()) {
            String errorMessage = "Payment not found, paymentId=\"" + paymentId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to edit payment.
        Payment payment = optionalPayment.get();
        PaymentForm paymentForm = new PaymentForm(payment);
        model.addAttribute("paymentForm", paymentForm);
        return "paymentEdit";
    }

    @PostMapping("/paymentEdit")
    public String processEdit(@Valid PaymentForm paymentForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "paymentEdit";
        }
        Long paymentId = paymentForm.getId();
        Payment payment;
        if (paymentId != null) {
            Optional<Payment> optionalPayment = paymentService.findPaymentById(paymentId);
            // If no payment, we gots an error.
            if (!optionalPayment.isPresent()) {
                String errorMessage = "Payment not found, paymentId=\"" + paymentId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            // Prepare to savePayment existing payment.
            payment = paymentForm.toPayment();
        } else {
            // If no payment id, prepare to savePayment new payment.
            payment = paymentForm.toPayment();
        }
        Long payableId = paymentForm.getPayableId();
        Optional<Payable> optionalPayable = payableService.findPayableById(payableId);
        // If no payable, we gots an error.
        if (!optionalPayable.isPresent()) {
            String errorMessage = "Payable not found, payableId=\"" + payableId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        payment.setPayable(optionalPayable.get());
        // Try to savePayment payment.
        Payment save;
        try {
            save = paymentService.savePayment(payment);
            String successMessage = "Payment successfully added, paymentId=\"" + save.getId() + "\"";
            if (paymentId != null)
                successMessage = "Payment successfully updated, paymentId=\"" + save.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("paymentId", save.getId());
        } catch (Exception e) {
            String errorMessage = "New payment could not be added";
            if (paymentId != null)
                errorMessage = "Payment could not be updated, paymentId=\"" + paymentId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
            if (paymentId == null) {
                return "redirect:/";
            } else {
                redirectAttributes.addAttribute("paymentId", paymentId);
                return "redirect:/payment?paymentId={paymentId}";
            }
        }
        return "redirect:/payment?paymentId={paymentId}";
    }
}