package norman.junk.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import norman.junk.domain.AcctNbr;

public interface AcctNbrRepository extends Repository<AcctNbr, Long> {
    List<AcctNbr> findTopByAcct_IdOrderByEffDateDesc(Long acctId);

    List<AcctNbr> findByAcct_FidAndNumber(String fid, String number);
}