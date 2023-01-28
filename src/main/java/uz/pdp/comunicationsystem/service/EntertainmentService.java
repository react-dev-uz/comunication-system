package uz.pdp.comunicationsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.pdp.comunicationsystem.entity.Entertainment;
import uz.pdp.comunicationsystem.payload.response.EntertainmentServiceDTO;
import uz.pdp.comunicationsystem.repository.EntertainmentServiceRepository;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Service
public class EntertainmentService {
    private final EntertainmentServiceRepository repository;

    @Autowired
    public EntertainmentService(EntertainmentServiceRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity<?> getAll() {
        return ok(repository.findAll());
    }

    public ResponseEntity<?> getOne(Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> add(EntertainmentServiceDTO dto) {
        if (repository.existsByName(dto.getName()))
            return status(UNPROCESSABLE_ENTITY).body("Name has already been registered");
        return status(CREATED).body(repository.save(new Entertainment(dto)));
    }

    public ResponseEntity<?> edit(Long id, EntertainmentServiceDTO dto) {
        return repository.findById(id)
                .map(entertainment -> {
                    if (repository.existsByNameAndIdNot(dto.getName(), id))
                        return status(UNPROCESSABLE_ENTITY).body("Name has already been registered");
                    return status(CREATED).body(repository.save(new Entertainment(dto)));
                })
                .orElseGet(() -> status(NOT_FOUND).body("Unfortunately, no such entertaining service was found"));
    }
}
