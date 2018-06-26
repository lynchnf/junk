package norman.junk.service;

import java.util.Optional;
import norman.junk.domain.Payment;
import norman.junk.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    public Iterable<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}