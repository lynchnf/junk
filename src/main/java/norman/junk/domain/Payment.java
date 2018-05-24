package norman.junk.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYABLE_ID", nullable = false)
    private Payable payable;
    @Temporal(TemporalType.DATE)
    private Date paidDate;
    @Column(precision = 9, scale = 2)
    private BigDecimal amountPaid;

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

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }
}