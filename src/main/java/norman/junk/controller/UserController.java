package norman.junk.controller;

import java.util.Optional;
import norman.junk.domain.User;
import norman.junk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
}