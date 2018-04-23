package norman.junk.repository;

import org.springframework.data.repository.CrudRepository;

import norman.junk.domain.DataFile;

public interface DataFileRepository extends CrudRepository<DataFile, Long> {}