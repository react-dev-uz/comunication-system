package uz.pdp.comunicationsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.comunicationsystem.entity.Detalizatsiya;
import uz.pdp.comunicationsystem.entity.enums.Action;
import uz.pdp.comunicationsystem.payload.response.DetailsDTO;
import uz.pdp.comunicationsystem.repository.DetalizatsiyaRepository;
import uz.pdp.comunicationsystem.repository.SimCardRepository;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.*;

@Service
public class DetalizatsiyaService {
    private final DetalizatsiyaRepository repository;
    private final SimCardRepository simCardRepository;

    @Autowired
    public DetalizatsiyaService(DetalizatsiyaRepository repository, SimCardRepository simCardRepository) {
        this.repository = repository;
        this.simCardRepository = simCardRepository;
    }

    public ResponseEntity<?> getAllDetails(Integer page, Integer size, String sort) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? page - 1 : 0, size > 1 ? size : 20, Sort.by(sort));
        return ok(repository.findAll(pageRequest));
    }

    public ResponseEntity<?> getOneSimCardDetails(UUID simCardId) {
        return repository.findAllBySimCard_Id(simCardId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> getAllActions() {
        return ok(Action.values());
    }

    @Transactional
    public ResponseEntity<?> addNewAction(DetailsDTO dto) {
        return simCardRepository.findById(dto.getSimCardId())
                .map(simCard -> {
                    boolean isExists = false;
                    for (Action value : Action.values()) {
                        if (value.name().equals(dto.getAction())) {
                            isExists = true;
                            break;
                        }
                    }
                    return !isExists ? badRequest().build() : ok(repository.save(new Detalizatsiya(Action.valueOf(dto.getAction()), dto.getAmount(), simCard)));
                })
                .orElseGet(() -> status(NOT_FOUND).body("Sim card not found"));
    }

//   // todo
    public ResponseEntity<?> exportDetailsToDocumentFormat(UUID simCardId, String type) {
        return null;
    }
}
