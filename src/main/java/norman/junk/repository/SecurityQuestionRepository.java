package norman.junk.repository;

import norman.junk.domain.SecurityQuestion;
import org.springframework.data.repository.CrudRepository;

public interface SecurityQuestionRepository extends CrudRepository<SecurityQuestion, Long> {
}