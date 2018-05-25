package norman.junk.service;

import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.Tran;
import norman.junk.repository.AcctNbrRepository;
import norman.junk.repository.AcctRepository;
import norman.junk.repository.TranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AcctService {
    @Autowired
    private AcctRepository acctRepository;
    @Autowired
    private AcctNbrRepository acctNbrRepository;
    @Autowired
    private TranRepository tranRepository;

    public Iterable<Acct> findAllAccts() {
        return acctRepository.findAll();
    }

    public Optional<Acct> findAcctById(Long acctId) {
        return acctRepository.findById(acctId);
    }

    public Acct saveAcct(Acct acct) {
        return acctRepository.save(acct);
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

    public List<TranBalanceBean> findTranBalancesByAcctId(Long acctId) {
        List<TranBalanceBean> tranBalances = new ArrayList<>();
        Optional<Acct> optionalAcct = acctRepository.findById(acctId);
        if (optionalAcct.isPresent()) {
            Acct acct = optionalAcct.get();
            BigDecimal balance = acct.getBeginBalance();
            List<Tran> trans = acct.getTrans();
            for (Tran tran : trans) {
                BigDecimal amount = tran.getAmount();
                balance = balance.add(amount);
                TranBalanceBean tranBalance = new TranBalanceBean(tran.getId(), tran.getType(), tran.getPostDate(), tran.getCheckNumber(), tran.getName(), tran.getMemo(), amount, balance);
                tranBalances.add(tranBalance);
            }
        }
        Collections.reverse(tranBalances);
        return tranBalances;
    }

    public List<AcctSummaryBean> findAllAcctSummaries() {
        List<AcctSummaryBean> acctSummaries = new ArrayList<>();
        Iterable<Acct> accts = acctRepository.findAll();
        for (Acct acct : accts) {
            BigDecimal balance = acct.getBeginBalance();
            Date lastTranDate = acct.getBeginDate();
            List<Tran> trans = acct.getTrans();
            for (Tran tran : trans) {
                balance = balance.add(tran.getAmount());
                if (lastTranDate.before(tran.getPostDate())) {
                    lastTranDate = tran.getPostDate();
                }
            }
            AcctSummaryBean acctSummary = new AcctSummaryBean(acct.getId(), acct.getName(), acct.getType(), balance, lastTranDate);
            acctSummaries.add(acctSummary);
        }
        return acctSummaries;
    }
}