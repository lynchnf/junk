package norman.junk.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class Payable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYEE_ID", nullable = false)
    private Payee payee;
    @Column(precision = 9, scale = 2)
    private BigDecimal previousBalance;
    @Column(precision = 9, scale = 2)
    private BigDecimal paymentsAndOtherCredits;
    @Column(precision = 9, scale = 2)
    private BigDecimal purchasesAndAdjustments;
    @Column(precision = 9, scale = 2)
    private BigDecimal feesCharged;
    @Column(precision = 9, scale = 2)
    private BigDecimal interestCharged;
    @Column(precision = 9, scale = 2)
    private BigDecimal newBalanceTotal;
    @Temporal(TemporalType.DATE)
    private Date statementClosingDate;
    @Column(precision = 9, scale = 2)
    private BigDecimal minimumPaymentDue;
    @Temporal(TemporalType.DATE)
    private Date paymentDueDate;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "payable")
    private List<Payment> payments = new ArrayList<>();

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

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }

    public BigDecimal getPaymentsAndOtherCredits() {
        return paymentsAndOtherCredits;
    }

    public void setPaymentsAndOtherCredits(BigDecimal paymentsAndOtherCredits) {
        this.paymentsAndOtherCredits = paymentsAndOtherCredits;
    }

    public BigDecimal getPurchasesAndAdjustments() {
        return purchasesAndAdjustments;
    }

    public void setPurchasesAndAdjustments(BigDecimal purchasesAndAdjustments) {
        this.purchasesAndAdjustments = purchasesAndAdjustments;
    }

    public BigDecimal getFeesCharged() {
        return feesCharged;
    }

    public void setFeesCharged(BigDecimal feesCharged) {
        this.feesCharged = feesCharged;
    }

    public BigDecimal getInterestCharged() {
        return interestCharged;
    }

    public void setInterestCharged(BigDecimal interestCharged) {
        this.interestCharged = interestCharged;
    }

    public BigDecimal getNewBalanceTotal() {
        return newBalanceTotal;
    }

    public void setNewBalanceTotal(BigDecimal newBalanceTotal) {
        this.newBalanceTotal = newBalanceTotal;
    }

    public Date getStatementClosingDate() {
        return statementClosingDate;
    }

    public void setStatementClosingDate(Date statementClosingDate) {
        this.statementClosingDate = statementClosingDate;
    }

    public BigDecimal getMinimumPaymentDue() {
        return minimumPaymentDue;
    }

    public void setMinimumPaymentDue(BigDecimal minimumPaymentDue) {
        this.minimumPaymentDue = minimumPaymentDue;
    }

    public Date getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(Date paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}