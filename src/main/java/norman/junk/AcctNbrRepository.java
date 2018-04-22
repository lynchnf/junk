package norman.junk;

import java.util.List;

import org.springframework.data.repository.Repository;

public interface AcctNbrRepository extends Repository<AcctNbr, Long> {
    List<AcctNbr> findTopByAcct_IdOrderByEffDateDesc(Long acctId);
}