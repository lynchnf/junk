package norman.junk.repository;

import norman.junk.domain.Pattern;
import org.springframework.data.repository.CrudRepository;

public interface PatternRepository extends CrudRepository<Pattern, Long> {
}