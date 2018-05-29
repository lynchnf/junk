package norman.junk.repository;

import java.util.List;
import norman.junk.domain.AcctNbr;
import org.springframework.data.repository.Repository;

public interface AcctNbrRepository extends Repository<AcctNbr, Long> {
    List<AcctNbr> findTopByAcct_IdOrderByEffDateDesc(Long acctId);

    List<AcctNbr> findByAcct_FidAndNumber(String fid, String number);
}