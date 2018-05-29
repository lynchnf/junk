package norman.junk.repository;

import java.util.List;
import norman.junk.domain.Acct;
import org.springframework.data.repository.CrudRepository;

public interface AcctRepository extends CrudRepository<Acct, Long> {
    List<Acct> findByFid(String fid);
}