package norman.junk.service;

import java.util.List;
import norman.junk.DatabaseException;
import norman.junk.domain.Pattern;
import norman.junk.repository.PatternRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatternService {
    private static final Logger logger = LoggerFactory.getLogger(PatternService.class);
    @Autowired
    private PatternRepository patternRepository;

    public List<Pattern> findAllPatterns() throws DatabaseException {
        try {
            return patternRepository.findAllByOrderBySeq();
        } catch (Exception e) {
            String msg = "Error finding all patterns ordered by seq";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }

    public Iterable<Pattern> saveAllPatterns(List<Pattern> patterns) throws DatabaseException {
        try {
            return patternRepository.saveAll(patterns);
        } catch (Exception e) {
            String msg = "Error saving patterns";
            logger.error(msg, e);
            throw new DatabaseException(msg, e);
        }
    }
}