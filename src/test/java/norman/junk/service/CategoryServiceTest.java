package norman.junk.service;

import java.util.ArrayList;
import java.util.Optional;
import norman.junk.FakeDataUtil;
import norman.junk.JunkNotFoundException;
import norman.junk.JunkOptimisticLockingException;
import norman.junk.domain.Category;
import norman.junk.repository.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
public class CategoryServiceTest {
    private Long category1Id;
    private Long category2Id;
    private Category category1;
    private String category1Name;

    @TestConfiguration
    static class CategoryServiceTestConfiguration {
        @Bean
        public CategoryService categoryService() {
            return new CategoryService();
        }
    }

    @Autowired
    private CategoryService categoryService;
    @MockBean
    private CategoryRepository categoryRepository;

    @Before
    public void setUp() throws Exception {
        category1Id = Long.valueOf(1);
        category2Id = Long.valueOf(2);
        category1 = FakeDataUtil.buildCategory(category1Id);
        category1Name = category1.getName();
    }

    @Test
    public void findAllCategories() {
        Mockito.when(categoryRepository.findAll()).thenReturn(new ArrayList<>());
        Iterable<Category> categories = categoryService.findAllCategories();
        assertFalse(categories.iterator().hasNext());
    }

    @Test
    public void findCategoryById() throws Exception {
        Mockito.when(categoryRepository.findById(category1Id)).thenReturn(Optional.of(category1));
        Category category = categoryService.findCategoryById(category1Id);
        assertEquals(category1Name, category.getName());
    }

    @Test
    public void findCategoryByIdNotFound() {
        Mockito.when(categoryRepository.findById(category2Id)).thenReturn(Optional.empty());
        try {
            Category category = categoryService.findCategoryById(category2Id);
            fail();
        } catch (JunkNotFoundException e) {
        }
    }

    @Test
    public void saveCategory() throws Exception {
        Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(category1);
        Category category = categoryService.saveCategory(category1);
        assertEquals(category1Name, category.getName());
    }

    @Test
    public void saveCategoryOptimisticLocking() {
        Mockito.when(categoryRepository.save(Mockito.any(Category.class)))
                .thenThrow(ObjectOptimisticLockingFailureException.class);
        try {
            Category category = categoryService.saveCategory(category1);
            fail();
        } catch (JunkOptimisticLockingException e) {
        }
    }
}