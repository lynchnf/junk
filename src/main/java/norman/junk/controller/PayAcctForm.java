package norman.junk.controller;

import javax.validation.constraints.Size;

import norman.junk.domain.PayAcct;

public class PayAcctForm {
    private Long id;
    private Integer version = 0;
    private Long payeeId;
    private String payeeName;
    @Size(max = 50)
    private String nickname;
    @Size(max = 50)
    private String number;

    public PayAcctForm() {}

    public PayAcctForm(PayAcct payAcct) {
        id = payAcct.getId();
        version = payAcct.getVersion();
        payeeId = payAcct.getPayee().getId();
        payeeName = payAcct.getPayee().getName();
        nickname = payAcct.getNickname();
        number = payAcct.getNumber();
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

    public Long getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Long payeeId) {
        this.payeeId = payeeId;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
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

    public PayAcct toPayAcct() {
        PayAcct payAcct = new PayAcct();
        payAcct.setId(id);
        payAcct.setVersion(version);
        payAcct.setNickname(nickname);
        payAcct.setNumber(number);
        return payAcct;
    }

    public Long toPayeeId() {
        return payeeId;
    }
}