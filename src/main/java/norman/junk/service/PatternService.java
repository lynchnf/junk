package norman.junk.service;

import norman.junk.domain.Pattern;
import norman.junk.repository.PatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatternService {
    @Autowired
    private PatternRepository patternRepository;

    public Iterable<Pattern> findAllPatterns() {
        return patternRepository.findAll();
    }
}