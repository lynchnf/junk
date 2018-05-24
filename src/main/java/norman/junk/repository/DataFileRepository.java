package norman.junk.repository;

import norman.junk.domain.DataFile;
import org.springframework.data.repository.CrudRepository;

public interface DataFileRepository extends CrudRepository<DataFile, Long> {
}