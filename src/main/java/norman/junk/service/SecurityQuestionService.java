package norman.junk.service;

import java.util.Optional;
import norman.junk.domain.SecurityQuestion;
import norman.junk.repository.SecurityQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityQuestionService {
    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;

    public Iterable<SecurityQuestion> findAllSecurityQuestions() {
        return securityQuestionRepository.findAll();
    }

    public Optional<SecurityQuestion> findSecurityQuestionById(Long securityQuestionId) {
        return securityQuestionRepository.findById(securityQuestionId);
    }

    public SecurityQuestion saveSecurityQuestion(SecurityQuestion securityQuestion) {
        return securityQuestionRepository.save(securityQuestion);
    }
}