package norman.junk.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
import norman.junk.domain.Category;
import norman.junk.domain.Pattern;
import norman.junk.service.CategoryService;
import norman.junk.validation.RegexPattern;

public class PatternRow {
    private Long id;
    private Integer version = 0;
    @NotNull
    private Long categoryId;
    @NotBlank
    @Size(max = 255)
    @RegexPattern
    private String tranName;

    public PatternRow() {
    }

    public PatternRow(Pattern pattern) {
        id = pattern.getId();
        version = pattern.getVersion();
        categoryId = pattern.getCategory().getId();
        tranName = pattern.getTranName();
    }

    public Pattern toPattern(CategoryService categoryService) throws DatabaseException, NotFoundException {
        Pattern pattern = new Pattern();
        pattern.setId(id);
        pattern.setVersion(version);
        Category category = categoryService.findCategoryById(categoryId);
        pattern.setCategory(category);
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