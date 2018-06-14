package norman.junk.controller;

import java.util.Optional;
import javax.validation.Valid;
import norman.junk.domain.SecurityQuestion;
import norman.junk.service.SecurityQuestionService;
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
public class SecurityQuestionController {
    private static final Logger logger = LoggerFactory.getLogger(SecurityQuestionController.class);
    @Autowired
    private SecurityQuestionService securityQuestionService;

    @RequestMapping("/securityQuestionList")
    public String loadList(Model model) {
        Iterable<SecurityQuestion> securityQuestions = securityQuestionService.findAllSecurityQuestions();
        model.addAttribute("securityQuestions", securityQuestions);
        return "securityQuestionList";
    }

    @RequestMapping("/securityQuestion")
    public String loadView(@RequestParam("securityQuestionId") Long securityQuestionId, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<SecurityQuestion> optionalSecurityQuestion = securityQuestionService
                .findSecurityQuestionById(securityQuestionId);
        // If no securityQuestion, we gots an error.
        if (!optionalSecurityQuestion.isPresent()) {
            String errorMessage = "SecurityQuestion not found, securityQuestionId=\"" + securityQuestionId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to view securityQuestion.
        SecurityQuestion securityQuestion = optionalSecurityQuestion.get();
        model.addAttribute("securityQuestion", securityQuestion);
        return "securityQuestionView";
    }

    @GetMapping("/securityQuestionEdit")
    public String loadEdit(@RequestParam(value = "securityQuestionId", required = false) Long securityQuestionId,
            Model model, RedirectAttributes redirectAttributes) {
        // If no securityQuestion id, new securityQuestion.
        if (securityQuestionId == null) {
            model.addAttribute("securityQuestionForm", new SecurityQuestionForm());
            return "securityQuestionEdit";
        }
        Optional<SecurityQuestion> optionalSecurityQuestion = securityQuestionService
                .findSecurityQuestionById(securityQuestionId);
        // If no securityQuestion, we gots an error.
        if (!optionalSecurityQuestion.isPresent()) {
            String errorMessage = "SecurityQuestion not found, securityQuestionId=\"" + securityQuestionId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to edit securityQuestion.
        SecurityQuestion securityQuestion = optionalSecurityQuestion.get();
        SecurityQuestionForm securityQuestionForm = new SecurityQuestionForm(securityQuestion);
        model.addAttribute("securityQuestionForm", securityQuestionForm);
        return "securityQuestionEdit";
    }

    @PostMapping("/securityQuestionEdit")
    public String processEdit(@Valid SecurityQuestionForm securityQuestionForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "securityQuestionEdit";
        }
        Long securityQuestionId = securityQuestionForm.getId();
        SecurityQuestion securityQuestion;
        if (securityQuestionId != null) {
            Optional<SecurityQuestion> optionalSecurityQuestion = securityQuestionService
                    .findSecurityQuestionById(securityQuestionId);
            // If no securityQuestion, we gots an error.
            if (!optionalSecurityQuestion.isPresent()) {
                String errorMessage = "SecurityQuestion not found, securityQuestionId=\"" + securityQuestionId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            // Prepare to savePayment existing securityQuestion.
            securityQuestion = securityQuestionForm.toSecurityQuestion();
        } else {
            // If no securityQuestion id, prepare to savePayment new securityQuestion.
            securityQuestion = securityQuestionForm.toSecurityQuestion();
        }
        // Try to savePayment securityQuestion.
        SecurityQuestion save;
        try {
            save = securityQuestionService.saveSecurityQuestion(securityQuestion);
            String successMessage = "SecurityQuestion successfully added, securityQuestionId=\"" + save.getId() + "\"";
            if (securityQuestionId != null)
                successMessage = "SecurityQuestion successfully updated, securityQuestionId=\"" + save.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("securityQuestionId", save.getId());
        } catch (Exception e) {
            String errorMessage = "New securityQuestion could not be added";
            if (securityQuestionId != null)
                errorMessage =
                        "SecurityQuestion could not be updated, securityQuestionId=\"" + securityQuestionId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
            if (securityQuestionId == null) {
                return "redirect:/";
            } else {
                redirectAttributes.addAttribute("securityQuestionId", securityQuestionId);
                return "redirect:/securityQuestion?securityQuestionId={securityQuestionId}";
            }
        }
        return "redirect:/securityQuestion?securityQuestionId={securityQuestionId}";
    }
}