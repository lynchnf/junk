package norman.junk.repository;

import java.util.List;
import norman.junk.domain.Payable;
import org.springframework.data.repository.CrudRepository;

public interface PayableRepository extends CrudRepository<Payable, Long> {
    List<Payable> findAllByOrderByDueDate();
}