package uz.pdp.comunicationsystem.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.payload.request.TariffDTO;
import uz.pdp.comunicationsystem.service.TariffService;

@RestController
@RequestMapping("/api/tariff")
public class TariffController {

    private final TariffService service;

    @Autowired
    public TariffController(TariffService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "sort", defaultValue = "id") String sort) {
        return service.tariffs(sort);
    }

    @GetMapping("/{tariffId}")
    public ResponseEntity<?> getOne(@PathVariable("tariffId") Long tariffId) {
        return service.tariff(tariffId);
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody TariffDTO tariffDTO) {
        return service.addTariff(tariffDTO);
    }

    @PutMapping("/{tariffId}")
    public ResponseEntity<?> update(@PathVariable("tariffId") Long tariffId,@Valid @RequestBody TariffDTO tariffDTO) {
        return service.editTariff(tariffId, tariffDTO);
    }

    @DeleteMapping("/{tariffId}")
    public ResponseEntity<?> delete(@PathVariable("tariffId") Long tariffId) {
        return service.deleteTariff(tariffId);
    }
}
