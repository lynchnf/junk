package norman.junk.controller;

import java.util.Optional;
import norman.junk.domain.SecurityQuestion;
import norman.junk.service.SecurityQuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
}