package norman.junk.service;

import java.util.List;
import java.util.Optional;
import norman.junk.JunkNotFoundException;
import norman.junk.JunkOptimisticLockingException;
import norman.junk.domain.Tran;
import norman.junk.repository.TranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class TranService {
    @Autowired
    TranRepository tranRepository;

    public Tran findTranById(Long tranId) throws JunkNotFoundException {
        Optional<Tran> optional = tranRepository.findById(tranId);
        if (!optional.isPresent()) {
            throw new JunkNotFoundException("Tran not found, tranId=\"" + tranId + "\"");
        }
        return optional.get();
    }

    public Iterable<Tran> saveAllTrans(Iterable<Tran> trans) throws JunkOptimisticLockingException {
        try {
            return tranRepository.saveAll(trans);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new JunkOptimisticLockingException("Optimistic locking failure while saving trans", e);
        }
    }

    public List<Tran> findAllNonAssigned() {
        return tranRepository.findByCategoryIsNull();
    }
}