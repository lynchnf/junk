package norman.junk.service;

import java.util.Optional;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
import norman.junk.domain.DataFile;
import norman.junk.repository.DataFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataFileService {
    private static final Logger logger = LoggerFactory.getLogger(DataFileService.class);
    @Autowired
    private DataFileRepository dataFileRepository;

    public Iterable<DataFile> findAllDataFiles() throws DatabaseException {
        try {
            return dataFileRepository.findAll();
        } catch (Exception e) {
            String msg = "Error finding all dataFiles";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public DataFile findDataFileById(Long dataFileId) throws DatabaseException, NotFoundException {
        Optional<DataFile> optional;
        try {
            optional = dataFileRepository.findById(dataFileId);
        } catch (Exception e) {
            String msg = "Error finding dataFile, dataFileId=\"" + dataFileId + "\"";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
        if (!optional.isPresent()) {
            String msg = "DataFile not found, dataFileId=\"" + dataFileId + "\"";
            logger.warn(msg);
            throw new NotFoundException(msg);
        }
        return optional.get();
    }

    public DataFile saveDataFile(DataFile dataFile) throws DatabaseException {
        try {
            return dataFileRepository.save(dataFile);
        } catch (Exception e) {
            String msg = "Error saving dataFile";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }
}