package norman.junk.service;

import java.util.List;
import norman.junk.NewOptimisticLockingException;
import norman.junk.domain.Pattern;
import norman.junk.repository.PatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class PatternService {
    @Autowired
    private PatternRepository patternRepository;

    public List<Pattern> findAllPatterns() {
        return patternRepository.findAllByOrderBySeq();
    }

    public Iterable<Pattern> saveAllPatterns(List<Pattern> patterns) throws NewOptimisticLockingException {
        try {
            return patternRepository.saveAll(patterns);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new NewOptimisticLockingException("Optimistic locking failure while saving patterns", e);
        }
    }
}