package norman.junk.controller;

import javax.validation.Valid;
import norman.junk.NewNotFoundException;
import norman.junk.NewOptimisticLockingException;
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

import static norman.junk.controller.MessagesConstants.NOT_FOUND_ERROR;
import static norman.junk.controller.MessagesConstants.OPTIMISTIC_LOCK_ERROR;
import static norman.junk.controller.MessagesConstants.SUCCESSFULLY_ADDED;
import static norman.junk.controller.MessagesConstants.SUCCESSFULLY_UPDATED;

@Controller
public class CategoryController {
    // FIXME REFACTOR
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
        try {
            Category category = categoryService.findCategoryById(categoryId);
            model.addAttribute("category", category);
            return "categoryView";
        } catch (NewNotFoundException e) {
            redirectAttributes
                    .addFlashAttribute("errorMessage", String.format(NOT_FOUND_ERROR, "Category", categoryId));
            return "redirect:/categoryList";
        }
    }

    @GetMapping("/categoryEdit")
    public String loadEdit(@RequestParam(value = "categoryId", required = false) Long categoryId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no category id, new category.
        if (categoryId == null) {
            model.addAttribute("categoryForm", new CategoryForm());
            return "categoryEdit";
        }
        // Otherwise, edit existing category.
        try {
            Category category = categoryService.findCategoryById(categoryId);
            CategoryForm categoryForm = new CategoryForm(category);
            model.addAttribute("categoryForm", categoryForm);
            return "categoryEdit";
        } catch (NewNotFoundException e) {
            redirectAttributes
                    .addFlashAttribute("errorMessage", String.format(NOT_FOUND_ERROR, "Category", categoryId));
            return "redirect:/categoryList";
        }
    }

    @PostMapping("/categoryEdit")
    public String processEdit(@Valid CategoryForm categoryForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "categoryEdit";
        }
        // Convert form to entity and save.
        Long categoryId = categoryForm.getId();
        Category category = categoryForm.toCategory();
        try {
            Category save = categoryService.saveCategory(category);
            String successMessage = String.format(SUCCESSFULLY_ADDED, "Category", save.getId());
            if (categoryId != null)
                successMessage = String.format(SUCCESSFULLY_UPDATED, "Category", save.getId());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("categoryId", save.getId());
            return "redirect:/category?categoryId={categoryId}";
        } catch (NewOptimisticLockingException e) {
            redirectAttributes
                    .addFlashAttribute("errorMessage", String.format(OPTIMISTIC_LOCK_ERROR, "Category", categoryId));
            redirectAttributes.addAttribute("categoryId", categoryId);
            return "redirect:/category?categoryId={categoryId}";
        }
    }
}