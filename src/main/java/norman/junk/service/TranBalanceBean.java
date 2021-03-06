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
    private Boolean reconciled;
    private BigDecimal amount;
    private BigDecimal balance;
    private String categoryName;

    public TranBalanceBean(Long id, TranType type, Date postDate, String checkNumber, String name, Boolean reconciled,
            BigDecimal amount, BigDecimal balance, String categoryName) {
        this.id = id;
        this.type = type;
        this.postDate = postDate;
        this.checkNumber = checkNumber;
        this.name = name;
        this.amount = amount;
        this.reconciled = reconciled;
        this.balance = balance;
        this.categoryName = categoryName;
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

    public boolean isReconciled() {
        if (reconciled != null)
            return reconciled.booleanValue();
        return false;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCategoryName() {
        return categoryName;
    }
}