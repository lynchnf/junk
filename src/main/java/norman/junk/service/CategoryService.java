package norman.junk.service;

import java.util.Optional;
import norman.junk.NewNotFoundException;
import norman.junk.NewUpdatedByAnotherException;
import norman.junk.domain.Category;
import norman.junk.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    @Autowired
    private CategoryRepository categoryRepository;

    public Iterable<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category findCategoryById(Long categoryId) throws NewNotFoundException {
        Optional<Category> optional = categoryRepository.findById(categoryId);
        if (!optional.isPresent()) {
            String msg = "Category not found, categoryId=\"" + categoryId + "\"";
            logger.warn(msg);
            throw new NewNotFoundException(msg);
        }
        return optional.get();
    }

    public Category saveCategory(Category category) throws NewUpdatedByAnotherException {
        try {
            return categoryRepository.save(category);
        } catch (ObjectOptimisticLockingFailureException e) {
            String msg = "Could not save category, categoryId=\"" + category.getId() + "\"";
            logger.warn(msg, e);
            throw new NewUpdatedByAnotherException(msg, e);
        }
    }
}