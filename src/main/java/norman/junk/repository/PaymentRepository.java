package norman.junk.repository;

import org.springframework.data.repository.CrudRepository;

import norman.junk.domain.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long> {}