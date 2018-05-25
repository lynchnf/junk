package norman.junk.controller;

import norman.junk.domain.Payment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

public class PaymentForm {
    private Long id;
    private Integer version = 0;
    private Long payableId;
    private String payeeDisplayName;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date payableDueDate;
    @Digits(integer = 7, fraction = 2)
    private BigDecimal payableAmountDue;
    @NotNull
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date paidDate;
    @NotNull
    @Digits(integer = 7, fraction = 2)
    private BigDecimal amountPaid;
    @Size(max = 50)
    private String confirmCode;
    @Size(max = 50)
    private String transNumber;

    public PaymentForm() {
    }

    public PaymentForm(Payment payment) {
        id = payment.getId();
        version = payment.getVersion();
        payableId = payment.getPayable().getId();
        if (StringUtils.isBlank(payment.getPayable().getPayee().getNickname())) {
            payeeDisplayName = payment.getPayable().getPayee().getName();
        } else {
            payeeDisplayName = payment.getPayable().getPayee().getNickname();
        }
        payableDueDate = payment.getPayable().getDueDate();
        payableAmountDue = payment.getPayable().getAmountDue();
        paidDate = payment.getPaidDate();
        amountPaid = payment.getAmountPaid();
        confirmCode = payment.getConfirmCode();
        transNumber = payment.getTransNumber();
    }

    public Payment toPayment() {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setVersion(version);
        payment.setPaidDate(paidDate);
        payment.setAmountPaid(amountPaid);
        payment.setConfirmCode(confirmCode);
        payment.setTransNumber(transNumber);
        return payment;
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

    public Long getPayableId() {
        return payableId;
    }

    public void setPayableId(Long payableId) {
        this.payableId = payableId;
    }

    public String getPayeeDisplayName() {
        return payeeDisplayName;
    }

    public void setPayeeDisplayName(String payeeDisplayName) {
        this.payeeDisplayName = payeeDisplayName;
    }

    public Date getPayableDueDate() {
        return payableDueDate;
    }

    public void setPayableDueDate(Date payableDueDate) {
        this.payableDueDate = payableDueDate;
    }

    public BigDecimal getPayableAmountDue() {
        return payableAmountDue;
    }

    public void setPayableAmountDue(BigDecimal payableAmountDue) {
        this.payableAmountDue = payableAmountDue;
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

    public String getConfirmCode() {
        return confirmCode;
    }

    public void setConfirmCode(String confirmCode) {
        this.confirmCode = confirmCode;
    }

    public String getTransNumber() {
        return transNumber;
    }

    public void setTransNumber(String transNumber) {
        this.transNumber = transNumber;
    }
}