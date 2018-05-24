package norman.junk.repository;

import norman.junk.domain.AcctNbr;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface AcctNbrRepository extends Repository<AcctNbr, Long> {
    List<AcctNbr> findTopByAcct_IdOrderByEffDateDesc(Long acctId);

    List<AcctNbr> findByAcct_FidAndNumber(String fid, String number);
}