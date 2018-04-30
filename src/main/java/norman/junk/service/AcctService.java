package norman.junk.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.Tran;
import norman.junk.repository.AcctNbrRepository;
import norman.junk.repository.AcctRepository;
import norman.junk.repository.TranRepository;

@Service
public class AcctService {
    @Autowired
    private AcctRepository acctRepository;
    @Autowired
    private AcctNbrRepository acctNbrRepository;
    @Autowired
    private TranRepository tranRepository;

    public Acct saveAcct(Acct acct) {
        return acctRepository.save(acct);
    }

    public Optional<Acct> findAcctById(Long acctId) {
        return acctRepository.findById(acctId);
    }

    public Iterable<Acct> findAllAccts() {
        return acctRepository.findAll();
    }

    public List<Acct> findAcctsByFid(String fid) {
        return acctRepository.findByFid(fid);
    }

    public Optional<AcctNbr> findCurrentAcctNbrByAcctId(Long acctId) {
        Optional<AcctNbr> optionalAcctNbr = Optional.empty();
        List<AcctNbr> acctNbrList = acctNbrRepository.findTopByAcct_IdOrderByEffDateDesc(acctId);
        if (!acctNbrList.isEmpty()) {
            AcctNbr acctNbr = acctNbrList.iterator().next();
            optionalAcctNbr = Optional.of(acctNbr);
        }
        return optionalAcctNbr;
    }

    public List<AcctNbr> findAcctNbrsByFidAndNumber(String fid, String number) {
        return acctNbrRepository.findByAcct_FidAndNumber(fid, number);
    }

    public List<Tran> findTransByAcctIdAndFitId(Long acctId, String fitId) {
        return tranRepository.findByAcct_IdAndFitId(acctId, fitId);
    }
}