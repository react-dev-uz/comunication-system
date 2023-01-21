package uz.pdp.comunicationsystem.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.pdp.comunicationsystem.entity.UssdCode;
import uz.pdp.comunicationsystem.repository.UssdCodeRepository;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.ResponseEntity.*;

@Service
public class UssdCodeService {
    private final UssdCodeRepository repository;

    @Autowired
    public UssdCodeService(UssdCodeRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity<?> getAll() {
        return ok(repository.findAll());
    }

    public ResponseEntity<?> getOne(Long ussdId) {
        return repository.findById(ussdId).map(ResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> addOne(@Valid UssdCode ussdCode) {
        if (repository.existsByCode(ussdCode.getCode()))
            return status(UNPROCESSABLE_ENTITY).body("Ussd code already registered");
        return status(CREATED).body(repository.save(new UssdCode(null, ussdCode.getCode(), ussdCode.getDescription())));
    }

    public ResponseEntity<?> editOne(Long ussdId, @Valid UssdCode uc) {
        Optional<UssdCode> optionalUssdCode = repository.findById(ussdId);
        if (optionalUssdCode.isEmpty()) return notFound().build();
        if (repository.existsByCodeAndIdNot(uc.getCode(), ussdId))
            return status(UNPROCESSABLE_ENTITY).body("Ussd code already exists");
        UssdCode ussdCode = optionalUssdCode.get();
        return status(CREATED).body(repository.save(new UssdCode(ussdCode.getId(), uc.getCode(), uc.getDescription())));
    }

    public ResponseEntity<?> deleteOne(Long ussdId) {
        return repository.findById(ussdId).map(ussdCode -> {
            try {
                repository.delete(ussdCode);
            } catch (Exception e) {
                return badRequest().build();
            }
            return noContent().build();
        }).orElseGet(() -> notFound().build());
    }
}
