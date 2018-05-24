package norman.junk.service;

import norman.junk.domain.Tran;
import norman.junk.repository.TranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TranService {
    @Autowired
    TranRepository tranRepository;

    public Optional<Tran> findById(Long tranId) {
        return tranRepository.findById(tranId);
    }
}