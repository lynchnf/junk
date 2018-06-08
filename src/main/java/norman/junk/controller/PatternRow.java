package norman.junk.controller;

import java.util.Optional;
import norman.junk.domain.Category;
import norman.junk.domain.Pattern;
import norman.junk.service.CategoryService;

public class PatternRow {
    private Long id;
    private Integer version = 0;
    private Long categoryId;
    private String tranName;

    public PatternRow() {
    }

    public PatternRow(Pattern pattern) {
        id = pattern.getId();
        version = pattern.getVersion();
        categoryId = pattern.getCategory().getId();
        tranName = pattern.getTranName();
    }

    public Pattern toPattern(CategoryService categoryService) {
        Pattern pattern = new Pattern();
        pattern.setId(id);
        pattern.setVersion(version);
        if (categoryId != null) {
            Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);
            if (optionalCategory.isPresent())
                pattern.setCategory(optionalCategory.get());
        }
        pattern.setTranName(tranName);
        return pattern;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTranName() {
        return tranName;
    }

    public void setTranName(String tranName) {
        this.tranName = tranName;
    }
}