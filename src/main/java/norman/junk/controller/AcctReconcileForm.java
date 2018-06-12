package norman.junk.controller;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

public class AcctReconcileForm {
    private Long acctId;
    private String acctName;
    @NotNull
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date reconcileDate;
    @NotNull
    @Digits(integer = 7, fraction = 2)
    private BigDecimal reconcileAmount;

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public Date getReconcileDate() {
        return reconcileDate;
    }

    public void setReconcileDate(Date reconcileDate) {
        this.reconcileDate = reconcileDate;
    }

    public BigDecimal getReconcileAmount() {
        return reconcileAmount;
    }

    public void setReconcileAmount(BigDecimal reconcileAmount) {
        this.reconcileAmount = reconcileAmount;
    }
}