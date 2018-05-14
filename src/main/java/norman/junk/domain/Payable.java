package norman.junk.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

@Entity
public class Payable {
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private Integer version = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYEE_ID", nullable = false)
    private Payee payee;
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    @Column(precision = 9, scale = 2)
    private BigDecimal amountDue;
    @Column(precision = 9, scale = 2)
    private BigDecimal previousBalance;
    @Column(precision = 9, scale = 2)
    private BigDecimal previousPaidAmount;
    @Temporal(TemporalType.DATE)
    private Date statementDate;

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

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }

    public BigDecimal getPreviousPaidAmount() {
        return previousPaidAmount;
    }

    public void setPreviousPaidAmount(BigDecimal previousPaidAmount) {
        this.previousPaidAmount = previousPaidAmount;
    }

    public Date getStatementDate() {
        return statementDate;
    }

    public void setStatementDate(Date statementDate) {
        this.statementDate = statementDate;
    }
}