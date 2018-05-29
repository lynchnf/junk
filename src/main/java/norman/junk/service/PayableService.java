package norman.junk.service;

import java.util.Optional;
import norman.junk.domain.Payable;
import norman.junk.repository.PayableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayableService {
    @Autowired
    private PayableRepository payableRepository;

    public Iterable<Payable> findAllPayables() {
        return payableRepository.findAll();
    }

    public Optional<Payable> findPayableById(Long payableId) {
        return payableRepository.findById(payableId);
    }

    public Payable savePayable(Payable payable) {
        return payableRepository.save(payable);
    }
}