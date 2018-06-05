package norman.junk.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import norman.junk.domain.Category;
import org.apache.commons.lang3.StringUtils;

public class CategoryForm {
    private Long id;
    private Integer version = 0;
    @NotBlank
    @Size(max = 50)
    private String name;

    public CategoryForm() {
    }

    public CategoryForm(Category category) {
        id = category.getId();
        version = category.getVersion();
        name = category.getName();
    }

    public Category toCategory() {
        Category category = new Category();
        category.setId(id);
        category.setVersion(version);
        category.setName(StringUtils.trimToNull(name));
        return category;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}