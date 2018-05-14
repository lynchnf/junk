package norman.junk.repository;

import org.springframework.data.repository.CrudRepository;

import norman.junk.domain.Payable;

public interface PayableRepository extends CrudRepository<Payable, Long> {}