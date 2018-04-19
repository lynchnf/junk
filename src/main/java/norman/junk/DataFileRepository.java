package norman.junk;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataFileRepository extends CrudRepository<DataFile, Long> {}