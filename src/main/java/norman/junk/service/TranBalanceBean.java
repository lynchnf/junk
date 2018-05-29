package norman.junk.service;

import java.math.BigDecimal;
import java.util.Date;
import norman.junk.domain.TranType;

public class TranBalanceBean {
    private Long id;
    private TranType type;
    private Date postDate;
    private String checkNumber;
    private String name;
    private String memo;
    private BigDecimal amount;
    private BigDecimal balance;

    public TranBalanceBean(Long id, TranType type, Date postDate, String checkNumber, String name, String memo, BigDecimal amount, BigDecimal balance) {
        this.id = id;
        this.type = type;
        this.postDate = postDate;
        this.checkNumber = checkNumber;
        this.name = name;
        this.memo = memo;
        this.amount = amount;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public TranType getType() {
        return type;
    }

    public Date getPostDate() {
        return postDate;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public String getName() {
        return name;
    }

    public String getMemo() {
        return memo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}