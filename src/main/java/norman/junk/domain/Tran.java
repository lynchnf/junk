package norman.junk.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class Tran {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acct_id", nullable = false)
    private Acct acct;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TranType type;
    @Temporal(TemporalType.DATE)
    private Date postDate;
    @Temporal(TemporalType.DATE)
    private Date userDate;
    @Column(precision = 9, scale = 2)
    private BigDecimal amount;
    @Column(length = 255)
    private String fitId;
    @Column(length = 10)
    private String sic;
    @Column(length = 10)
    private String checkNumber;
    @Column(length = 255)
    private String correctFitId;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private CorrectAction correctAction;
    @Column(length = 255)
    private String name;
    @Column(length = 10)
    private String ofxCategory;
    @Column(length = 255)
    private String memo;
    private Boolean reconciled;

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

    public Acct getAcct() {
        return acct;
    }

    public void setAcct(Acct acct) {
        this.acct = acct;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public TranType getType() {
        return type;
    }

    public void setType(TranType type) {
        this.type = type;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getUserDate() {
        return userDate;
    }

    public void setUserDate(Date userDate) {
        this.userDate = userDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFitId() {
        return fitId;
    }

    public void setFitId(String fitId) {
        this.fitId = fitId;
    }

    public String getSic() {
        return sic;
    }

    public void setSic(String sic) {
        this.sic = sic;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getCorrectFitId() {
        return correctFitId;
    }

    public void setCorrectFitId(String correctFitId) {
        this.correctFitId = correctFitId;
    }

    public CorrectAction getCorrectAction() {
        return correctAction;
    }

    public void setCorrectAction(CorrectAction correctAction) {
        this.correctAction = correctAction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfxCategory() {
        return ofxCategory;
    }

    public void setOfxCategory(String ofxCategory) {
        this.ofxCategory = ofxCategory;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Boolean getReconciled() {
        return reconciled;
    }

    public void setReconciled(Boolean reconciled) {
        this.reconciled = reconciled;
    }
}