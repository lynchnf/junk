package norman.junk;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AcctForm {
    private Long id;
    private Integer version;
    @NotNull
    @Size(min = 1, max = 10)
    private String name;

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