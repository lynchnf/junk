package norman.junk.service;

import java.util.Optional;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
import norman.junk.domain.Payment;
import norman.junk.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    @Autowired
    PaymentRepository paymentRepository;

    public Iterable<Payment> findAllPayments() throws DatabaseException {
        try {
            return paymentRepository.findAll();
        } catch (Exception e) {
            String msg = "Error finding all payments";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public Payment findPaymentById(Long paymentId) throws DatabaseException, NotFoundException {
        Optional<Payment> optional;
        try {
            optional = paymentRepository.findById(paymentId);
        } catch (Exception e) {
            String msg = "Error finding payment, paymentId=\"" + paymentId + "\"";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
        if (!optional.isPresent()) {
            String msg = "Payment not found, paymentId=\"" + paymentId + "\"";
            logger.warn(msg);
            throw new NotFoundException(msg);
        }
        return optional.get();
    }

    public Payment savePayment(Payment payment) throws DatabaseException {
        try {
            return paymentRepository.save(payment);
        } catch (Exception e) {
            String msg = "Error saving payment";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }
}