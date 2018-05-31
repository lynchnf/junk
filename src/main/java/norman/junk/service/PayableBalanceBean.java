package norman.junk.service;

import java.math.BigDecimal;
import java.util.Date;

public class PayableBalanceBean {
    private Long id;
    private String payeeDisplayName;
    private Date paymentDueDate;
    private BigDecimal newBalanceTotal;
    private BigDecimal minimumPaymentDue;
    private BigDecimal balance;
    private Date lastPaymentDate;
    private String styleClass;

    public PayableBalanceBean(Long id, String payeeDisplayName, Date paymentDueDate, BigDecimal newBalanceTotal,
            BigDecimal minimumPaymentDue, BigDecimal balance, Date lastPaymentDate, String styleClass) {
        this.id = id;
        this.payeeDisplayName = payeeDisplayName;
        this.paymentDueDate = paymentDueDate;
        this.newBalanceTotal = newBalanceTotal;
        this.minimumPaymentDue = minimumPaymentDue;
        this.balance = balance;
        this.lastPaymentDate = lastPaymentDate;
        this.styleClass = styleClass;
    }

    public Long getId() {
        return id;
    }

    public String getPayeeDisplayName() {
        return payeeDisplayName;
    }

    public Date getPaymentDueDate() {
        return paymentDueDate;
    }

    public BigDecimal getNewBalanceTotal() {
        return newBalanceTotal;
    }

    public BigDecimal getMinimumPaymentDue() {
        return minimumPaymentDue;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public String getStyleClass() {
        return styleClass;
    }
}