package norman.junk.service;

import java.util.Optional;
import norman.junk.NewNotFoundException;
import norman.junk.NewOptimisticLockingException;
import norman.junk.domain.Category;
import norman.junk.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Iterable<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category findCategoryById(Long categoryId) throws NewNotFoundException {
        Optional<Category> optional = categoryRepository.findById(categoryId);
        if (!optional.isPresent()) {
            throw new NewNotFoundException("Category not found, categoryId=\"" + categoryId + "\"");
        }
        return optional.get();
    }

    public Category saveCategory(Category category) throws NewOptimisticLockingException {
        try {
            return categoryRepository.save(category);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new NewOptimisticLockingException(
                    "Optimistic locking failure while saving category, categoryId=\"" + category.getId() + "\"", e);
        }
    }
}