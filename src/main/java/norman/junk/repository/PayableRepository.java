package norman.junk.repository;

import norman.junk.domain.Payable;
import org.springframework.data.repository.CrudRepository;

public interface PayableRepository extends CrudRepository<Payable, Long> {
}