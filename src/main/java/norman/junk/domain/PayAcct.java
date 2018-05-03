package norman.junk.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
public class PayAcct {
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private Integer version = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYEE_ID", nullable = false)
    private Payee payee;
    @Column(length = 50)
    private String nickname;
    @Column(length = 50)
    private String number;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "payAcct")
    private List<Payable> payables = new ArrayList<>();

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

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<Payable> getPayables() {
        return payables;
    }

    public void setPayables(List<Payable> payables) {
        this.payables = payables;
    }
}