package norman.junk.service;

import java.util.List;
import java.util.Optional;
import norman.junk.DatabaseException;
import norman.junk.NotFoundException;
import norman.junk.domain.Tran;
import norman.junk.repository.TranRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranService {
    private static final Logger logger = LoggerFactory.getLogger(TranService.class);
    @Autowired
    TranRepository tranRepository;

    public Tran findTranById(Long tranId) throws DatabaseException, NotFoundException {
        Optional<Tran> optional;
        try {
            optional = tranRepository.findById(tranId);
        } catch (Exception e) {
            String msg = "Error finding tran, tranId=\"" + tranId + "\"";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
        if (!optional.isPresent()) {
            String msg = "Tran not found, tranId=\"" + tranId + "\"";
            logger.warn(msg);
            throw new NotFoundException(msg);
        }
        return optional.get();
    }

    public Iterable<Tran> saveAllTrans(Iterable<Tran> trans) throws DatabaseException {
        try {
            return tranRepository.saveAll(trans);
        } catch (Exception e) {
            String msg = "Error saving trans";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public List<Tran> findAllNonAssigned() throws DatabaseException {
        try {
            return tranRepository.findByCategoryIsNull();
        } catch (Exception e) {
            String msg = "Error finding trans with no category";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }
}