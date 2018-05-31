package norman.junk.controller;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import norman.junk.domain.Payable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

public class PayableForm {
    private Long id;
    private Integer version = 0;
    private Long payeeId;
    private String payeeDisplayName;
    @Min(0)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal previousBalance;
    @Max(0)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal paymentsAndOtherCredits;
    @Min(0)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal purchasesAndAdjustments;
    @Min(0)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal feesCharged;
    @Min(0)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal interestCharged;
    @NotNull
    @Min(0)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal newBalanceTotal;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date statementClosingDate;
    @Min(0)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal minimumPaymentDue;
    @NotNull
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date paymentDueDate;

    public PayableForm() {
    }

    public PayableForm(Payable payable) {
        id = payable.getId();
        version = payable.getVersion();
        payeeId = payable.getPayee().getId();
        if (StringUtils.isBlank(payable.getPayee().getNickname())) {
            payeeDisplayName = payable.getPayee().getName();
        } else {
            payeeDisplayName = payable.getPayee().getNickname();
        }
        previousBalance = payable.getPreviousBalance();
        paymentsAndOtherCredits = payable.getPaymentsAndOtherCredits();
        purchasesAndAdjustments = payable.getPurchasesAndAdjustments();
        feesCharged = payable.getFeesCharged();
        interestCharged = payable.getInterestCharged();
        newBalanceTotal = payable.getNewBalanceTotal();
        statementClosingDate = payable.getStatementClosingDate();
        minimumPaymentDue = payable.getMinimumPaymentDue();
        paymentDueDate = payable.getPaymentDueDate();
    }

    public Payable toPayable() {
        Payable payable = new Payable();
        payable.setId(id);
        payable.setVersion(version);
        payable.setPreviousBalance(previousBalance);
        payable.setPaymentsAndOtherCredits(paymentsAndOtherCredits);
        payable.setPurchasesAndAdjustments(purchasesAndAdjustments);
        payable.setFeesCharged(feesCharged);
        payable.setInterestCharged(interestCharged);
        payable.setNewBalanceTotal(newBalanceTotal);
        payable.setStatementClosingDate(statementClosingDate);
        payable.setMinimumPaymentDue(minimumPaymentDue);
        payable.setPaymentDueDate(paymentDueDate);
        return payable;
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

    public Long getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Long payeeId) {
        this.payeeId = payeeId;
    }

    public String getPayeeDisplayName() {
        return payeeDisplayName;
    }

    public void setPayeeDisplayName(String payeeDisplayName) {
        this.payeeDisplayName = payeeDisplayName;
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
}