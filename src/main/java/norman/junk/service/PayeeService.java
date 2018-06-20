package norman.junk.service;

import java.util.Optional;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
import norman.junk.domain.Payee;
import norman.junk.repository.PayeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayeeService {
    private static final Logger logger = LoggerFactory.getLogger(PayeeService.class);
    @Autowired
    private PayeeRepository payeeRepository;

    public Iterable<Payee> findAllPayees() throws DatabaseException {
        try {
            return payeeRepository.findAll();
        } catch (Exception e) {
            String msg = "Error finding all payees";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public Payee findPayeeById(Long payeeId) throws DatabaseException, NotFoundException {
        Optional<Payee> optional;
        try {
            optional = payeeRepository.findById(payeeId);
        } catch (Exception e) {
            String msg = "Error finding payee, payeeId=\"" + payeeId + "\"";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
        if (!optional.isPresent()) {
            String msg = "Payee not found, payeeId=\"" + payeeId + "\"";
            logger.warn(msg);
            throw new NotFoundException(msg);
        }
        return optional.get();
    }

    public Payee savePayee(Payee payee) throws DatabaseException {
        try {
            return payeeRepository.save(payee);
        } catch (Exception e) {
            String msg = "Error saving payee";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }
}