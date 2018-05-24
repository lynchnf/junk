package norman.junk.service;

import norman.junk.domain.DataFile;
import norman.junk.repository.DataFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DataFileService {
    @Autowired
    private DataFileRepository dataFileRepository;

    public DataFile saveDataFile(DataFile dataFile) {
        return dataFileRepository.save(dataFile);
    }

    public Optional<DataFile> findDataFileById(Long dataFileId) {
        return dataFileRepository.findById(dataFileId);
    }

    public Iterable<DataFile> findAllDataFiles() {
        return dataFileRepository.findAll();
    }
}