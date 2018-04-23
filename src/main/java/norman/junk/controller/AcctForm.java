package norman.junk.controller;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import norman.junk.domain.AcctType;
import norman.junk.validation.AfterDateIfValueChange;

@AfterDateIfValueChange(newDate = "effDate", oldDate = "oldEffDate", newString = "acctNbr", oldString = "oldAcctNbr")
public class AcctForm {
    private Long id;
    private Integer version;
    @NotBlank
    private String name;
    @NotNull
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date beginDate;
    @NotNull
    @Digits(integer = 3, fraction = 2)
    private BigDecimal beginBalance;
    private String organization;
    private String fid;
    private String bankId;
    @NotNull
    private AcctType type;
    @NotBlank
    private String acctNbr;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date effDate;
    private String oldAcctNbr;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date oldEffDate;

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

    public String getAcctNbr() {
        return acctNbr;
    }

    public void setAcctNbr(String acctNbr) {
        this.acctNbr = acctNbr;
    }

    public String getOldAcctNbr() {
        return oldAcctNbr;
    }

    public void setOldAcctNbr(String oldAcctNbr) {
        this.oldAcctNbr = oldAcctNbr;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public Date getOldEffDate() {
        return oldEffDate;
    }

    public void setOldEffDate(Date oldEffDate) {
        this.oldEffDate = oldEffDate;
    }
}