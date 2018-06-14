package norman.junk.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import norman.junk.domain.SecurityQuestion;

public class SecurityQuestionForm {
    private Long id;
    private Integer version = 0;
    @NotBlank
    @Size(max = 255)
    private String questionText;

    public SecurityQuestionForm() {
    }

    public SecurityQuestionForm(SecurityQuestion securityQuestion) {
        id = securityQuestion.getId();
        version = securityQuestion.getVersion();
        questionText = securityQuestion.getQuestionText();
    }

    public SecurityQuestion toSecurityQuestion() {
        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setId(id);
        securityQuestion.setVersion(version);
        securityQuestion.setQuestionText(questionText);
        return securityQuestion;
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

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}