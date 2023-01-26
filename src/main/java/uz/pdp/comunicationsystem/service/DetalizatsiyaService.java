package uz.pdp.comunicationsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.pdp.comunicationsystem.repository.DetalizatsiyaRepository;

import static org.springframework.http.ResponseEntity.ok;

@Service
public class DetalizatsiyaService {
    private final DetalizatsiyaRepository repository;

    @Autowired
    public DetalizatsiyaService(DetalizatsiyaRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity<?> getAllDetails(Integer page, Integer size, String sort) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? page - 1 : 0, size > 1 ? size : 20, Sort.by(sort));
        return ok(repository.findAll(pageRequest));
    }

}
