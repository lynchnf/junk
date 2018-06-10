package norman.junk.controller;

import java.util.Optional;
import javax.validation.Valid;
import norman.junk.domain.Category;
import norman.junk.service.CategoryService;
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
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/categoryList")
    public String loadList(Model model) {
        Iterable<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);
        return "categoryList";
    }

    @RequestMapping("/category")
    public String loadView(@RequestParam("categoryId") Long categoryId, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);
        // If no category, we gots an error.
        if (!optionalCategory.isPresent()) {
            String errorMessage = "Category not found, categoryId=\"" + categoryId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to view category.
        Category category = optionalCategory.get();
        model.addAttribute("category", category);
        return "categoryView";
    }

    @GetMapping("/categoryEdit")
    public String loadEdit(@RequestParam(value = "categoryId", required = false) Long categoryId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no category id, new category.
        if (categoryId == null) {
            model.addAttribute("categoryForm", new CategoryForm());
            return "categoryEdit";
        }
        Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);
        // If no category, we gots an error.
        if (!optionalCategory.isPresent()) {
            String errorMessage = "Category not found, categoryId=\"" + categoryId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to edit category.
        Category category = optionalCategory.get();
        CategoryForm categoryForm = new CategoryForm(category);
        model.addAttribute("categoryForm", categoryForm);
        return "categoryEdit";
    }

    @PostMapping("/categoryEdit")
    public String processEdit(@Valid CategoryForm categoryForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "categoryEdit";
        }
        Long categoryId = categoryForm.getId();
        Category category;
        if (categoryId != null) {
            Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);
            // If no category, we gots an error.
            if (!optionalCategory.isPresent()) {
                String errorMessage = "Category not found, categoryId=\"" + categoryId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            // Prepare to savePayment existing category.
            category = categoryForm.toCategory();
        } else {
            // If no category id, prepare to savePayment new category.
            category = categoryForm.toCategory();
        }
        // Try to savePayment category.
        Category save;
        try {
            save = categoryService.saveCategory(category);
            String successMessage = "Category successfully added, categoryId=\"" + save.getId() + "\"";
            if (categoryId != null)
                successMessage = "Category successfully updated, categoryId=\"" + save.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("categoryId", save.getId());
        } catch (Exception e) {
            String errorMessage = "New category could not be added";
            if (categoryId != null)
                errorMessage = "Category could not be updated, categoryId=\"" + categoryId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
            if (categoryId == null) {
                return "redirect:/";
            } else {
                redirectAttributes.addAttribute("categoryId", categoryId);
                return "redirect:/category?categoryId={categoryId}";
            }
        }
        return "redirect:/category?categoryId={categoryId}";
    }
}