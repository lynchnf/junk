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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYABLE_ID", nullable = false)
    private Payable payable;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAN_ID", nullable = false)
    private Tran tran;
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

    public Payable getPayable() {
        return payable;
    }

    public void setPayable(Payable payable) {
        this.payable = payable;
    }

    public Tran getTran() {
        return tran;
    }

    public void setTran(Tran tran) {
        this.tran = tran;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}