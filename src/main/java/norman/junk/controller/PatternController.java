package norman.junk.controller;

import norman.junk.domain.Pattern;
import norman.junk.service.CategoryService;
import norman.junk.service.PatternService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PatternController {
    private static final Logger logger = LoggerFactory.getLogger(PatternController.class);
    @Autowired
    private PatternService patternService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/patternList")
    public String loadList(Model model) {
        Iterable<Pattern> patterns = patternService.findAllPatterns();
        model.addAttribute("patterns", patterns);
        return "patternList";
    }
}