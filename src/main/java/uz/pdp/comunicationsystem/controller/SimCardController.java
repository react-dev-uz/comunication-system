package uz.pdp.comunicationsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.service.SimCardService;

import java.util.UUID;

@RestController
@RequestMapping("/api/sim-card")
public class SimCardController {
    private final SimCardService service;

    @Autowired
    public SimCardController(SimCardService service) {
        this.service = service;
    }

    @GetMapping("/ussd-code")
    public ResponseEntity<?> getSimData(@RequestParam(name = "ussd") String ussdCode) {
        return service.simUssdCodeService(ussdCode);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getActiveSimCards(@RequestParam(name = "q", defaultValue = "active") String active) {
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return service.getAll(page, size);
    }

    @GetMapping("/{simCardId}")
    public ResponseEntity<?> getOne(@PathVariable(name = "simCardId") UUID id) {
        return service.getOne(id);
    }

    @GetMapping("/change-tariff/{tariffId}")
    public ResponseEntity<?> changeTariff(@PathVariable(name = "tariffId") UUID tariffId) {
        return null;
    }
}
