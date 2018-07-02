package norman.junk.service;

import java.util.Optional;
import norman.junk.NewNotFoundException;
import norman.junk.NewOptimisticLockingException;
import norman.junk.domain.DataFile;
import norman.junk.repository.DataFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class DataFileService {
    @Autowired
    private DataFileRepository dataFileRepository;

    public Iterable<DataFile> findAllDataFiles() {
        return dataFileRepository.findAll();
    }

    public DataFile findDataFileById(Long dataFileId) throws NewNotFoundException {
        Optional<DataFile> optional = dataFileRepository.findById(dataFileId);
        if (!optional.isPresent()) {
            throw new NewNotFoundException("DataFile not found, dataFileId=\"" + dataFileId + "\"");
        }
        return optional.get();
    }

    public DataFile saveDataFile(DataFile dataFile) throws NewOptimisticLockingException {
        try {
            return dataFileRepository.save(dataFile);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new NewOptimisticLockingException(
                    "Optimistic locking failure while saving dataFile, dataFileId=\"" + dataFile.getId() + "\"", e);
        }
    }
}