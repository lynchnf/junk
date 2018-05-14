package norman.junk.repository;

import org.springframework.data.repository.CrudRepository;

import norman.junk.domain.Payee;

public interface PayeeRepository extends CrudRepository<Payee, Long> {}