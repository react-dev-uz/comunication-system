package uz.pdp.comunicationsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.comunicationsystem.entity.Tariff;
import uz.pdp.comunicationsystem.payload.request.TariffDTO;
import uz.pdp.comunicationsystem.repository.TariffRepository;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.ResponseEntity.*;

@Service
public class TariffService {
    private final TariffRepository repository;

    @Autowired
    public TariffService(TariffRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity<?> tariffs(String sort) {
        return ok(repository.findAll(Sort.by(sort)));
    }

    public ResponseEntity<?> tariff(Long tariffId) {
        return repository.findById(tariffId).map(ResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> addTariff(TariffDTO dto) {
        if (repository.existsByName(dto.getName()))
            return status(UNPROCESSABLE_ENTITY).body("Tariff nomi oldin ro'yhatga olingan");
        return status(CREATED).body(repository.save(new Tariff(null, dto)));
    }

    public ResponseEntity<?> editTariff(Long tariffId, TariffDTO dto) {
        return repository.findById(tariffId).map(tariff -> {
            if (repository.existsByNameAndIdNot(dto.getName(), tariffId)) return status(UNPROCESSABLE_ENTITY).body("");
            return ok(repository.save(new Tariff(tariffId, dto)));
        }).orElseGet(() -> notFound().build());
    }

    @Transactional
    public ResponseEntity<?> deleteTariff(Long id) {
        return repository.findById(id).map(tariff -> {
            repository.delete(tariff);
            return noContent().build();
        }).orElseGet(() -> notFound().build());
    }
}
