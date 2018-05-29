package norman.junk.repository;

import java.util.List;
import norman.junk.domain.Tran;
import org.springframework.data.repository.CrudRepository;

public interface TranRepository extends CrudRepository<Tran, Long> {
    List<Tran> findByAcct_IdAndFitId(Long acctId, String fitId);
}