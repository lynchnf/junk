package norman.junk.repository;

import java.util.List;
import norman.junk.domain.Pattern;
import org.springframework.data.repository.CrudRepository;

public interface PatternRepository extends CrudRepository<Pattern, Long> {
    List<Pattern> findAllByOrderBySeq();
}