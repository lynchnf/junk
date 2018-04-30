package norman.junk.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
public class Acct {
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private Integer version = 0;
    @Column(length = 50)
    private String name;
    @Temporal(TemporalType.DATE)
    private Date beginDate;
    @Column(precision = 9, scale = 2)
    private BigDecimal beginBalance;
    @Column(length = 50)
    private String organization;
    @Column(length = 20)
    private String fid;
    @Column(length = 20)
    private String bankId;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private AcctType type;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "acct")
    private List<AcctNbr> acctNbrs = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "acct")
    private List<Tran> trans = new ArrayList<>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public BigDecimal getBeginBalance() {
        return beginBalance;
    }

    public void setBeginBalance(BigDecimal beginBalance) {
        this.beginBalance = beginBalance;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public AcctType getType() {
        return type;
    }

    public void setType(AcctType type) {
        this.type = type;
    }

    public List<AcctNbr> getAcctNbrs() {
        return acctNbrs;
    }

    public void setAcctNbrs(List<AcctNbr> acctNbrs) {
        this.acctNbrs = acctNbrs;
    }

    public List<Tran> getTrans() {
        return trans;
    }

    public void setTrans(List<Tran> trans) {
        this.trans = trans;
    }
}