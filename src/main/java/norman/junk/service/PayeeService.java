package norman.junk.service;

import java.util.Optional;
import norman.junk.domain.Payee;
import norman.junk.repository.PayeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayeeService {
    @Autowired
    private PayeeRepository payeeRepository;

    public Iterable<Payee> findAllPayees() {
        return payeeRepository.findAll();
    }

    public Optional<Payee> findPayeeById(Long payeeId) {
        return payeeRepository.findById(payeeId);
    }

    public Payee savePayee(Payee payee) {
        return payeeRepository.save(payee);
    }
}