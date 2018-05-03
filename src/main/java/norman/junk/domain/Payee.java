package norman.junk.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
public class Payee {
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private Integer version = 0;
    @Column(length = 50)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "payee")
    private List<PayAcct> payAccts = new ArrayList<>();

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

    public List<PayAcct> getPayAccts() {
        return payAccts;
    }

    public void setPayAccts(List<PayAcct> payAccts) {
        this.payAccts = payAccts;
    }
}