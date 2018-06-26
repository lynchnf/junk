package norman.junk.service;

import java.math.BigDecimal;
import java.util.Date;

public class PayableDueBean {
    private Long id;
    private String payeeDisplayName;
    private Date dueDate;
    private BigDecimal amountDue;
    private BigDecimal minimumPayment;
    private Date lastPaidDate;
    private BigDecimal balanceDue;
    private String styleClass;

    public PayableDueBean(Long id, String payeeDisplayName, Date dueDate, BigDecimal amountDue,
            BigDecimal minimumPayment, Date lastPaidDate, BigDecimal balanceDue, String styleClass) {
        this.id = id;
        this.payeeDisplayName = payeeDisplayName;
        this.dueDate = dueDate;
        this.amountDue = amountDue;
        this.minimumPayment = minimumPayment;
        this.lastPaidDate = lastPaidDate;
        this.balanceDue = balanceDue;
        this.styleClass = styleClass;
    }

    public Long getId() {
        return id;
    }

    public String getPayeeDisplayName() {
        return payeeDisplayName;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public BigDecimal getMinimumPayment() {
        return minimumPayment;
    }

    public Date getLastPaidDate() {
        return lastPaidDate;
    }

    public BigDecimal getBalanceDue() {
        return balanceDue;
    }

    public String getStyleClass() {
        return styleClass;
    }
}