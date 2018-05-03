package norman.junk.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import norman.junk.domain.Payee;

public class PayeeForm {
    private Long id;
    private Integer version = 0;
    @NotBlank
    @Size(max = 50)
    private String name;

    public PayeeForm() {}

    public PayeeForm(Payee payee) {
        id = payee.getId();
        version = payee.getVersion();
        name = payee.getName();
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

    public Payee toPayee() {
        Payee payee = new Payee();
        payee.setId(id);
        payee.setVersion(version);
        payee.setName(name);
        return payee;
    }
}