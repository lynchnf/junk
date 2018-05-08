package norman.junk.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import norman.junk.domain.Payee;

public class PayeeForm {
    private Long id;
    private Integer version = 0;
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotBlank
    @Size(max = 50)
    private String number;
    @Size(max = 50)
    private String nickname;
    @Size(max = 50)
    private String address1;
    @Size(max = 50)
    private String address2;
    @Size(max = 50)
    private String city;
    @Size(max = 2)
    private String state;
    @Size(max = 10)
    private String zipCode;
    @Size(max = 20)
    private String phoneNumber;

    public PayeeForm() {}

    public PayeeForm(Payee payee) {
        id = payee.getId();
        version = payee.getVersion();
        name = payee.getName();
        number = payee.getNumber();
        nickname = payee.getNickname();
        address1 = payee.getAddress1();
        address2 = payee.getAddress2();
        city = payee.getCity();
        state = payee.getState();
        zipCode = payee.getZipCode();
        phoneNumber = payee.getPhoneNumber();
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Payee toPayee() {
        Payee payee = new Payee();
        payee.setId(id);
        payee.setVersion(version);
        payee.setName(name);
        payee.setNumber(number);
        payee.setNickname(nickname);
        payee.setAddress1(address1);
        payee.setAddress2(address2);
        payee.setCity(city);
        payee.setState(state);
        payee.setZipCode(zipCode);
        payee.setPhoneNumber(phoneNumber);
        return payee;
    }
}