package norman.junk.controller;

import javax.validation.Valid;
import norman.junk.domain.Category;
import norman.junk.domain.Pattern;
import norman.junk.repository.PatternRepository;
import norman.junk.service.CategoryService;
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
    @Autowired
    private PatternRepository patternService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/patternList")
    public String loadList(Model model) {
        Iterable<Pattern> patterns = patternService.findAll();
        model.addAttribute("patterns", patterns);
        return "patternList";
    }

    @GetMapping("/patternEdit")
    public String loadEdit(Model model, RedirectAttributes redirectAttributes) {
        Iterable<Pattern> patterns = patternService.findAll();
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
        return "redirect:/patternList";
    }

    @ModelAttribute("allCategories")
    public Iterable<Category> loadCategoryDropDown() {
        return categoryService.findAllCategories();
    }
}