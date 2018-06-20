package norman.junk.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.Tran;
import norman.junk.repository.AcctNbrRepository;
import norman.junk.repository.AcctRepository;
import norman.junk.repository.TranRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AcctService {
    private static final Logger logger = LoggerFactory.getLogger(AcctService.class);
    @Autowired
    private AcctRepository acctRepository;
    @Autowired
    private AcctNbrRepository acctNbrRepository;
    @Autowired
    private TranRepository tranRepository;

    public Iterable<Acct> findAllAccts() throws DatabaseException {
        try {
            return acctRepository.findAll();
        } catch (Exception e) {
            String msg = "Error with acctRepository.findAll()";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public Acct findAcctById(Long acctId) throws DatabaseException, NotFoundException {
        Optional<Acct> optional;
        try {
            optional = acctRepository.findById(acctId);
        } catch (Exception e) {
            String msg = "Error with acctRepository.findById(" + acctId + ")";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
        if (!optional.isPresent()) {
            String msg = "Acct not found, acctRepository.findById(" + acctId + ")";
            logger.warn(msg);
            throw new NotFoundException(msg);
        }
        return optional.get();
    }

    public Acct saveAcct(Acct acct) throws DatabaseException {
        try {
            return acctRepository.save(acct);
        } catch (Exception e) {
            String msg = "Error with acctRepository.save(acct)";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public List<Acct> findAcctsByFid(String fid) throws DatabaseException {
        try {
            return acctRepository.findByFid(fid);
        } catch (Exception e) {
            String msg = "Error with acctRepository.findByFid(" + fid + ")";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public AcctNbr findCurrentAcctNbrByAcctId(Long acctId) throws DatabaseException, NotFoundException {
        List<AcctNbr> acctNbrList;
        try {
            acctNbrList = acctNbrRepository.findTopByAcct_IdOrderByEffDateDesc(acctId);
        } catch (Exception e) {
            String msg = "Error with acctNbrRepository.findTopByAcct_IdOrderByEffDateDesc(" + acctId + ")";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
        if (acctNbrList.isEmpty()) {
            String msg = "AcctNbr not found, acctNbrRepository.findTopByAcct_IdOrderByEffDateDesc(" + acctId + ")";
            logger.warn(msg);
            throw new NotFoundException(msg);
        }
        return acctNbrList.iterator().next();
    }

    public List<AcctNbr> findAcctNbrsByFidAndNumber(String fid, String number) throws DatabaseException {
        try {
            return acctNbrRepository.findByAcct_FidAndNumber(fid, number);
        } catch (Exception e) {
            String msg = "Error with acctNbrRepository.findByAcct_FidAndNumber(" + fid + ", " + number + ")";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public List<Tran> findTransByAcctIdAndFitId(Long acctId, String fitId) throws DatabaseException {
        try {
            return tranRepository.findByAcct_IdAndFitId(acctId, fitId);
        } catch (Exception e) {
            String msg = "Error with tranRepository.findByAcct_IdAndFitId(" + acctId + ", " + fitId + ")";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public List<TranBalanceBean> findTranBalancesByAcctId(Long acctId) throws DatabaseException, NotFoundException {
        List<TranBalanceBean> tranBalances = new ArrayList<>();
        Acct acct = findAcctById(acctId);
        BigDecimal balance = acct.getBeginBalance();
        List<Tran> trans = acct.getTrans();
        for (Tran tran : trans) {
            BigDecimal amount = tran.getAmount();
            balance = balance.add(amount);
            String acctName = null;
            if (tran.getAcct() != null)
                acctName = tran.getAcct().getName();
            TranBalanceBean tranBalance = new TranBalanceBean(tran.getId(), tran.getType(), tran.getPostDate(),
                    tran.getCheckNumber(), tran.getName(), tran.getReconciled(), amount, balance, acctName);
            tranBalances.add(tranBalance);
        }
        Collections.reverse(tranBalances);
        return tranBalances;
    }

    public List<AcctSummaryBean> findAllAcctSummaries() throws DatabaseException {
        List<AcctSummaryBean> acctSummaries = new ArrayList<>();
        Iterable<Acct> accts = findAllAccts();
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
            AcctSummaryBean acctSummary = new AcctSummaryBean(acct.getId(), acct.getName(), acct.getType(),
                    acct.getCreditLimit(), balance, lastTranDate);
            acctSummaries.add(acctSummary);
        }
        return acctSummaries;
    }
}