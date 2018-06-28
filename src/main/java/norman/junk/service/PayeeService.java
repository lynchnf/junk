package norman.junk.service;

import java.util.Optional;
import norman.junk.NewNotFoundException;
import norman.junk.NewUpdatedByAnotherException;
import norman.junk.domain.Payee;
import norman.junk.repository.PayeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class PayeeService {
    private static final Logger logger = LoggerFactory.getLogger(PayeeService.class);
    @Autowired
    private PayeeRepository payeeRepository;

    public Iterable<Payee> findAllPayees() {
        return payeeRepository.findAll();
    }

    public Payee findPayeeById(Long payeeId) throws NewNotFoundException {
        Optional<Payee> optional = payeeRepository.findById(payeeId);
        if (!optional.isPresent()) {
            String msg = "Payee not found, payeeId=\"" + payeeId + "\"";
            logger.warn(msg);
            throw new NewNotFoundException(msg);
        }
        return optional.get();
    }

    public Payee savePayee(Payee payee) throws NewUpdatedByAnotherException {
        try {
            return payeeRepository.save(payee);
        } catch (ObjectOptimisticLockingFailureException e) {
            String msg = "Could not save payee, payeeId=\"" + payee.getId() + "\"";
            logger.warn(msg, e);
            throw new NewUpdatedByAnotherException(msg, e);
        }
    }
}