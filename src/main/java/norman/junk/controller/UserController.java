package norman.junk.controller;

import java.util.Optional;
import javax.validation.Valid;
import norman.junk.domain.User;
import norman.junk.service.UserService;
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
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @RequestMapping("/userList")
    public String loadList(Model model) {
        Iterable<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "userList";
    }

    @RequestMapping("/user")
    public String loadView(@RequestParam("userId") Long userId, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findUserById(userId);
        // If no user, we gots an error.
        if (!optionalUser.isPresent()) {
            String errorMessage = "User not found, userId=\"" + userId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to view user.
        User user = optionalUser.get();
        model.addAttribute("user", user);
        return "userView";
    }

    @GetMapping("/userEdit")
    public String loadEdit(@RequestParam(value = "userId", required = false) Long userId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no user id, new user.
        if (userId == null) {
            model.addAttribute("userForm", new UserForm());
            return "userEdit";
        }
        Optional<User> optionalUser = userService.findUserById(userId);
        // If no user, we gots an error.
        if (!optionalUser.isPresent()) {
            String errorMessage = "User not found, userId=\"" + userId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to edit user.
        User user = optionalUser.get();
        UserForm userForm = new UserForm(user);
        model.addAttribute("userForm", userForm);
        return "userEdit";
    }

    @PostMapping("/userEdit")
    public String processEdit(@Valid UserForm userForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "userEdit";
        }
        Long userId = userForm.getId();
        User user;
        if (userId != null) {
            Optional<User> optionalUser = userService.findUserById(userId);
            // If no user, we gots an error.
            if (!optionalUser.isPresent()) {
                String errorMessage = "User not found, userId=\"" + userId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            // Prepare to savePayment existing user.
            user = userForm.toUser();
        } else {
            // If no user id, prepare to savePayment new user.
            user = userForm.toUser();
        }
        // Try to savePayment user.
        User save;
        try {
            save = userService.saveUser(user);
            String successMessage = "User successfully added, userId=\"" + save.getId() + "\"";
            if (userId != null)
                successMessage = "User successfully updated, userId=\"" + save.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("userId", save.getId());
        } catch (Exception e) {
            String errorMessage = "New user could not be added";
            if (userId != null)
                errorMessage = "User could not be updated, userId=\"" + userId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
            if (userId == null) {
                return "redirect:/";
            } else {
                redirectAttributes.addAttribute("userId", userId);
                return "redirect:/user?userId={userId}";
            }
        }
        return "redirect:/user?userId={userId}";
    }
}