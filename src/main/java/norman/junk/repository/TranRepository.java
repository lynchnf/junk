package norman.junk.repository;

import norman.junk.domain.Tran;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface TranRepository extends Repository<Tran, Long> {
    List<Tran> findByAcct_IdAndFitId(Long acctId, String fitId);
}