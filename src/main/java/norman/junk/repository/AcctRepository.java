package norman.junk.repository;

import norman.junk.domain.Acct;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AcctRepository extends CrudRepository<Acct, Long> {
    List<Acct> findByFid(String fid);
}