package norman.junk.service;

import java.util.List;
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

    public Iterable<Tran> saveAllTrans(Iterable<Tran> trans) {
        return tranRepository.saveAll(trans);
    }

    public List<Tran> findAllNonAssigned() {
        return tranRepository.findByCategoryIsNull();
    }
}