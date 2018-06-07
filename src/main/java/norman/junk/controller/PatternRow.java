package norman.junk.controller;

import norman.junk.domain.Pattern;

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