package norman.junk.controller;

import javax.validation.Valid;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
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
    private static final String DATABASE_ERROR = "Unexpected Database Error.";
    private static final String CATEGORY_NOT_FOUND = "Category not found.";
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/categoryList")
    public String loadList(Model model, RedirectAttributes redirectAttributes) {
        Iterable<Category> categories;
        try {
            categories = categoryService.findAllCategories();
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        model.addAttribute("categories", categories);
        return "categoryList";
    }

    @RequestMapping("/category")
    public String loadView(@RequestParam("categoryId") Long categoryId, Model model,
            RedirectAttributes redirectAttributes) {
        Category category;
        try {
            category = categoryService.findCategoryById(categoryId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", CATEGORY_NOT_FOUND);
            return "redirect:/";
        }
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
        // Otherwise, edit existing category.
        Category category;
        try {
            category = categoryService.findCategoryById(categoryId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", CATEGORY_NOT_FOUND);
            return "redirect:/";
        }
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
        // Verify existing category still exists.
        if (categoryId != null) {
            try {
                categoryService.findCategoryById(categoryId);
            } catch (DatabaseException e) {
                redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
                return "redirect:/";
            } catch (NotFoundException e) {
                redirectAttributes.addFlashAttribute("errorMessage", CATEGORY_NOT_FOUND);
                return "redirect:/";
            }
        }
        // Convert form to entity and save.
        category = categoryForm.toCategory();
        Category save;
        try {
            save = categoryService.saveCategory(category);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        String successMessage = "Category successfully added, categoryId=\"" + save.getId() + "\"";
        if (categoryId != null)
            successMessage = "Category successfully updated, categoryId=\"" + save.getId() + "\"";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        redirectAttributes.addAttribute("categoryId", save.getId());
        return "redirect:/category?categoryId={categoryId}";
    }
}