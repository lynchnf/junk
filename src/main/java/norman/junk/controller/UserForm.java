package norman.junk.controller;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import norman.junk.domain.User;
import norman.junk.domain.UserRole;
import org.apache.commons.lang3.StringUtils;

public class UserForm {
    private Long id;
    private Integer version = 0;
    @NotBlank
    @Size(max = 50)
    @Email
    private String username;
    @NotBlank
    @Size(max = 50)
    private String password;
    @NotNull
    private UserRole role;
    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String lastName;

    public UserForm() {
    }

    public UserForm(User user) {
        id = user.getId();
        version = user.getVersion();
        username = user.getUsername();
        password = user.getPassword();
        role = user.getRole();
        firstName = user.getFirstName();
        lastName = user.getLastName();
    }

    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setVersion(version);
        user.setUsername(StringUtils.trimToNull(username));
        user.setPassword(password);
        user.setRole(role);
        user.setFirstName(StringUtils.trimToNull(firstName));
        user.setLastName(StringUtils.trimToNull(lastName));
        return user;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}