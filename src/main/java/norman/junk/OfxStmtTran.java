package norman.junk;

import java.math.BigDecimal;
import java.util.Date;

public class OfxStmtTran {
    private TranType type;
    private Date postDate;
    private Date userDate;
    private BigDecimal amount;
    private String fitId;
    private String sic;
    private String checkNumber;
    private String correctFitId;
    private CorrectAction correctAction;
    private String name;
    private String category;
    private String memo;

    public void setType(TranType type) {
        this.type = type;
    }

    public TranType getType() {
        return type;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setUserDate(Date userDate) {
        this.userDate = userDate;
    }

    public Date getUserDate() {
        return userDate;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setFitId(String fitId) {
        this.fitId = fitId;
    }

    public String getFitId() {
        return fitId;
    }

    public void setSic(String sic) {
        this.sic = sic;
    }

    public String getSic() {
        return sic;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCorrectFitId(String correctFitId) {
        this.correctFitId = correctFitId;
    }

    public String getCorrectFitId() {
        return correctFitId;
    }

    public void setCorrectAction(CorrectAction correctAction) {
        this.correctAction = correctAction;
    }

    public CorrectAction getCorrectAction() {
        return correctAction;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return memo;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}