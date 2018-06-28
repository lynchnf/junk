package norman.junk.service;

import java.util.Optional;
import norman.junk.NewNotFoundException;
import norman.junk.NewUpdatedByAnotherException;
import norman.junk.domain.Payment;
import norman.junk.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    @Autowired
    private PaymentRepository paymentRepository;

    public Iterable<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment findPaymentById(Long paymentId) throws NewNotFoundException {
        Optional<Payment> optional = paymentRepository.findById(paymentId);
        if (!optional.isPresent()) {
            String msg = "Payment not found, paymentId=\"" + paymentId + "\"";
            logger.warn(msg);
            throw new NewNotFoundException(msg);
        }
        return optional.get();
    }

    public Payment savePayment(Payment payment) throws NewUpdatedByAnotherException {
        try {
            return paymentRepository.save(payment);
        } catch (ObjectOptimisticLockingFailureException e) {
            String msg = "Could not save payment, paymentId=\"" + payment.getId() + "\"";
            logger.warn(msg, e);
            throw new NewUpdatedByAnotherException(msg, e);
        }
    }
}