package norman.junk.service;

import java.util.Optional;
import norman.junk.NewNotFoundException;
import norman.junk.NewOptimisticLockingException;
import norman.junk.domain.Payee;
import norman.junk.repository.PayeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class PayeeService {
    @Autowired
    private PayeeRepository payeeRepository;

    public Iterable<Payee> findAllPayees() {
        return payeeRepository.findAll();
    }

    public Payee findPayeeById(Long payeeId) throws NewNotFoundException {
        Optional<Payee> optional = payeeRepository.findById(payeeId);
        if (!optional.isPresent()) {
            throw new NewNotFoundException("Payee not found, payeeId=\"" + payeeId + "\"");
        }
        return optional.get();
    }

    public Payee savePayee(Payee payee) throws NewOptimisticLockingException {
        try {
            return payeeRepository.save(payee);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new NewOptimisticLockingException(
                    "Optimistic locking failure while saving payee, payeeId=\"" + payee.getId() + "\"", e);
        }
    }
}