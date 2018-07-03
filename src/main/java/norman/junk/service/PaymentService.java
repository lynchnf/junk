package norman.junk.service;

import java.util.Optional;
import norman.junk.JunkNotFoundException;
import norman.junk.JunkOptimisticLockingException;
import norman.junk.domain.Payment;
import norman.junk.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public Iterable<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment findPaymentById(Long paymentId) throws JunkNotFoundException {
        Optional<Payment> optional = paymentRepository.findById(paymentId);
        if (!optional.isPresent()) {
            throw new JunkNotFoundException("Payment not found, paymentId=\"" + paymentId + "\"");
        }
        return optional.get();
    }

    public Payment savePayment(Payment payment) throws JunkOptimisticLockingException {
        try {
            return paymentRepository.save(payment);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new JunkOptimisticLockingException(
                    "Optimistic locking failure while saving payment, paymentId=\"" + payment.getId() + "\"", e);
        }
    }
}