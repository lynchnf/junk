package norman.junk.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
public class AcctNbr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCT_ID", nullable = false)
    private Acct acct;
    @Column(length = 50)
    private String number;
    @Temporal(TemporalType.DATE)
    private Date effDate;

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

    @Override
    public String toString() {
        return "AcctNbr{" + "id=" + id + ", version=" + version + ", number='" + number + '\'' + ", effDate=" + effDate + '}';
    }
}