package norman.junk.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import norman.junk.domain.Acct;

public interface AcctRepository extends CrudRepository<Acct, Long> {
    List<Acct> findByFid(String fid);
}