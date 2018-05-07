package norman.junk.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import norman.junk.domain.PayAcct;
import norman.junk.repository.PayAcctRepository;

@Service
public class PayAcctService {
    @Autowired
    private PayAcctRepository payAcctRepository;

    public Iterable<PayAcct> findAllPayAccts() {
        return payAcctRepository.findAll();
    }

    public Optional<PayAcct> findPayAcctById(Long payAcctId) {
        return payAcctRepository.findById(payAcctId);
    }

    public PayAcct savePayAcct(PayAcct payAcct) {
        return payAcctRepository.save(payAcct);
    }
}