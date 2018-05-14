package norman.junk.domain;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private Integer version = 0;
    @Column(precision = 9, scale = 2)
    private BigDecimal amount;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}