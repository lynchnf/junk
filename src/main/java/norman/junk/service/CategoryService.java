package norman.junk.service;

import java.util.Optional;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
import norman.junk.domain.Category;
import norman.junk.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    @Autowired
    private CategoryRepository categoryRepository;

    public Iterable<Category> findAllCategories() throws DatabaseException {
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            String msg = "Error finding all categories";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public Category findCategoryById(Long categoryId) throws DatabaseException, NotFoundException {
        Optional<Category> optional;
        try {
            optional = categoryRepository.findById(categoryId);
        } catch (Exception e) {
            String msg = "Error finding category, categoryId=\"" + categoryId + "\"";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
        if (!optional.isPresent()) {
            String msg = "Category not found, categoryId=\"" + categoryId + "\"";
            logger.warn(msg);
            throw new NotFoundException(msg);
        }
        return optional.get();
    }

    public Category saveCategory(Category category) throws DatabaseException {
        try {
            return categoryRepository.save(category);
        } catch (Exception e) {
            String msg = "Error saving category";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }
}