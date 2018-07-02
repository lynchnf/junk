package norman.junk.controller;

import java.util.List;
import javax.validation.Valid;
import norman.junk.NewOptimisticLockingException;
import norman.junk.domain.Category;
import norman.junk.domain.Pattern;
import norman.junk.service.CategoryService;
import norman.junk.service.PatternService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static norman.junk.controller.MessagesConstants.MULTI_OPTIMISTIC_LOCK_ERROR;

@Controller
public class PatternController {
    // FIXME REFACTOR
    private static final Logger logger = LoggerFactory.getLogger(PatternController.class);
    @Autowired
    private PatternService patternService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/patternList")
    public String loadList(Model model, RedirectAttributes redirectAttributes) {
        Iterable<Pattern> patterns = patternService.findAllPatterns();
        model.addAttribute("patterns", patterns);
        return "patternList";
    }

    @GetMapping("/patternEdit")
    public String loadEdit(Model model, RedirectAttributes redirectAttributes) {
        Iterable<Pattern> patterns = patternService.findAllPatterns();
        PatternForm patternForm = new PatternForm(patterns);
        model.addAttribute("patternForm", patternForm);
        return "patternEdit";
    }

    @PostMapping("/patternEdit")
    public String processEdit(@Valid PatternForm patternForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "patternEdit";
        }
        List<Pattern> patterns = patternForm.toPatterns(categoryService);
        try {
            Iterable<Pattern> saveAll = patternService.saveAllPatterns(patterns);
            String successMessage = "Category Patterns successfully saved";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/patternList";
        } catch (NewOptimisticLockingException e) {
            redirectAttributes
                    .addFlashAttribute("errorMessage", String.format(MULTI_OPTIMISTIC_LOCK_ERROR, "Patterns"));
            return "redirect:/patternList";
        }
    }

    @ModelAttribute("allCategories")
    public Iterable<Category> loadCategoryDropDown() {
        return categoryService.findAllCategories();
    }
}