package norman.junk.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import norman.junk.domain.Tran;

public interface TranRepository extends Repository<Tran, Long> {
    List<Tran> findByAcct_IdAndFitId(Long acctId, String fitId);
}