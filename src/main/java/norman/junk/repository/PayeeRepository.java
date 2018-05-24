package norman.junk.repository;

import norman.junk.domain.Payee;
import org.springframework.data.repository.CrudRepository;

public interface PayeeRepository extends CrudRepository<Payee, Long> {
}