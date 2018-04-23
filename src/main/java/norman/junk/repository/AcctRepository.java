package norman.junk.repository;

import org.springframework.data.repository.CrudRepository;

import norman.junk.domain.Acct;

public interface AcctRepository extends CrudRepository<Acct, Long> {}