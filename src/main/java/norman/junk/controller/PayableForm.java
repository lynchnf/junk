package norman.junk.controller;

import norman.junk.domain.Payable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

public class PayableForm {
    private Long id;
    private Integer version = 0;
    @NotNull
    private Long payeeId;
    private String payeeDisplayName;
    @NotNull
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date dueDate;
    @NotNull
    @Digits(integer = 7, fraction = 2)
    private BigDecimal amountDue;
    @Digits(integer = 7, fraction = 2)
    private BigDecimal previousBalance;
    @Digits(integer = 7, fraction = 2)
    private BigDecimal previousPaidAmount;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date statementDate;

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
        dueDate = payable.getDueDate();
        amountDue = payable.getAmountDue();
        previousBalance = payable.getPreviousBalance();
        previousPaidAmount = payable.getPreviousPaidAmount();
        statementDate = payable.getStatementDate();
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

    public Payable toPayable() {
        Payable payable = new Payable();
        payable.setId(id);
        payable.setVersion(version);
        payable.setDueDate(dueDate);
        payable.setAmountDue(amountDue);
        payable.setPreviousBalance(previousBalance);
        payable.setPreviousPaidAmount(previousPaidAmount);
        payable.setStatementDate(statementDate);
        return payable;
    }
}