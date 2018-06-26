package norman.junk.service;

import java.math.BigDecimal;
import java.util.Date;
import norman.junk.domain.AcctType;

public class AcctSummaryBean {
    private Long id;
    private String name;
    private AcctType type;
    private BigDecimal creditLimit;
    private BigDecimal balance;
    private Date lastTranDate;

    public AcctSummaryBean(Long id, String name, AcctType type, BigDecimal creditLimit, BigDecimal balance,
            Date lastTranDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.creditLimit = creditLimit;
        this.balance = balance;
        this.lastTranDate = lastTranDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AcctType getType() {
        return type;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Date getLastTranDate() {
        return lastTranDate;
    }
}