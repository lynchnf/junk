package norman.junk.service;

import java.util.Optional;
import norman.junk.domain.Tran;
import norman.junk.repository.TranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranService {
    @Autowired
    TranRepository tranRepository;

    public Optional<Tran> findTranById(Long tranId) {
        return tranRepository.findById(tranId);
    }
}