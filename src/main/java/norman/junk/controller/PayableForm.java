package norman.junk.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import norman.junk.domain.Payable;
import norman.junk.domain.Payee;
import norman.junk.service.PayeeService;
import org.springframework.format.annotation.DateTimeFormat;

public class PayableForm {
    private Long id;
    private Integer version = 0;
    @NotNull
    private Long payeeId;
    @NotNull
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date dueDate;
    @NotNull
    @Digits(integer = 7, fraction = 2)
    private BigDecimal amountDue;
    @Digits(integer = 7, fraction = 2)
    private BigDecimal previousBalance;
    @Max(0)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal previousPayments;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date statementDate;
    @Min(0)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal minimumPayment;

    public PayableForm() {
    }

    public PayableForm(Payable payable) {
        id = payable.getId();
        version = payable.getVersion();
        payeeId = payable.getPayee().getId();
        dueDate = payable.getDueDate();
        amountDue = payable.getAmountDue();
        previousBalance = payable.getPreviousBalance();
        previousPayments = payable.getPreviousPayments();
        statementDate = payable.getStatementDate();
        minimumPayment = payable.getMinimumPayment();
    }

    public Payable toPayable(PayeeService payeeService) {
        Payable payable = new Payable();
        payable.setId(id);
        payable.setVersion(version);
        if (payeeId != null) {
            Optional<Payee> optionalPayee = payeeService.findPayeeById(payeeId);
            if (optionalPayee.isPresent())
                payable.setPayee(optionalPayee.get());
        }
        payable.setDueDate(dueDate);
        payable.setAmountDue(amountDue);
        payable.setPreviousBalance(previousBalance);
        payable.setPreviousPayments(previousPayments);
        payable.setStatementDate(statementDate);
        payable.setMinimumPayment(minimumPayment);
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

    public BigDecimal getPreviousPayments() {
        return previousPayments;
    }

    public void setPreviousPayments(BigDecimal previousPayments) {
        this.previousPayments = previousPayments;
    }

    public Date getStatementDate() {
        return statementDate;
    }

    public void setStatementDate(Date statementDate) {
        this.statementDate = statementDate;
    }

    public BigDecimal getMinimumPayment() {
        return minimumPayment;
    }

    public void setMinimumPayment(BigDecimal minimumPayment) {
        this.minimumPayment = minimumPayment;
    }
}