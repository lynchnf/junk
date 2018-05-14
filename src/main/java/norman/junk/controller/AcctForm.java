package norman.junk.controller;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.AcctType;
import norman.junk.validation.AfterDateIfValueChange;

@AfterDateIfValueChange(newDate = "effDate", oldDate = "oldEffDate", newString = "number", oldString = "oldNumber")
public class AcctForm {
    private Long id;
    private Integer version = 0;
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotNull
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date beginDate;
    @NotNull
    @Digits(integer = 7, fraction = 2)
    private BigDecimal beginBalance;
    @Size(max = 50)
    private String organization;
    @Size(max = 20)
    private String fid;
    @Size(max = 20)
    private String bankId;
    @NotNull
    private AcctType type;
    @NotBlank
    @Size(max = 50)
    private String number;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date effDate;
    private String oldNumber;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date oldEffDate;
    private Long dataFileId;

    public AcctForm() {}

    public AcctForm(Acct acct, AcctNbr acctNbr) {
        id = acct.getId();
        version = acct.getVersion();
        name = acct.getName();
        beginDate = acct.getBeginDate();
        beginBalance = acct.getBeginBalance();
        organization = acct.getOrganization();
        fid = acct.getFid();
        bankId = acct.getBankId();
        type = acct.getType();
        number = acctNbr.getNumber();
        oldNumber = acctNbr.getNumber();
        effDate = acctNbr.getEffDate();
        oldEffDate = acctNbr.getEffDate();
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public String getOldNumber() {
        return oldNumber;
    }

    public void setOldNumber(String oldNumber) {
        this.oldNumber = oldNumber;
    }

    public Date getOldEffDate() {
        return oldEffDate;
    }

    public void setOldEffDate(Date oldEffDate) {
        this.oldEffDate = oldEffDate;
    }

    public Long getDataFileId() {
        return dataFileId;
    }

    public void setDataFileId(Long dataFileId) {
        this.dataFileId = dataFileId;
    }

    public Acct toAcct() {
        Acct acct = new Acct();
        acct.setId(id);
        acct.setVersion(version);
        acct.setName(name);
        acct.setBeginDate(beginDate);
        acct.setBeginBalance(beginBalance);
        acct.setOrganization(organization);
        acct.setFid(fid);
        acct.setBankId(bankId);
        acct.setType(type);
        return acct;
    }

    public AcctNbr toAcctNbr() {
        AcctNbr acctNbr = new AcctNbr();
        acctNbr.setNumber(number);
        acctNbr.setEffDate(effDate);
        return acctNbr;
    }
}