package norman.junk.controller;

import java.util.List;
import javax.validation.Valid;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
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

@Controller
public class PatternController {
    private static final Logger logger = LoggerFactory.getLogger(PatternController.class);
    private static final String DATABASE_ERROR = "Unexpected Database Error.";
    private static final String PATTERN_NOT_FOUND = "Pattern not found.";
    private static final String CATEGORY_NOT_FOUND = "Category not found.";
    @Autowired
    private PatternService patternService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/patternList")
    public String loadList(Model model, RedirectAttributes redirectAttributes) {
        Iterable<Pattern> patterns;
        try {
            patterns = patternService.findAllPatterns();
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        model.addAttribute("patterns", patterns);
        return "patternList";
    }

    @GetMapping("/patternEdit")
    public String loadEdit(Model model, RedirectAttributes redirectAttributes) {
        Iterable<Pattern> patterns;
        try {
            patterns = patternService.findAllPatterns();
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
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
        try {
            List<Pattern> patterns = patternForm.toPatterns(categoryService);
            Iterable<Pattern> saveAll = patternService.saveAllPatterns(patterns);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", CATEGORY_NOT_FOUND);
            return "redirect:/";
        }
        String successMessage = "Category Patterns successfully saved";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/patternList";
    }

    @ModelAttribute("allCategories")
    public Iterable<Category> loadCategoryDropDown() throws DatabaseException {
        // FIXME Handle exception somehow.
        return categoryService.findAllCategories();
    }
}