package uz.pdp.comunicationsystem.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.payload.request.SimCardDTO;
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

    @Secured({"ROLE_CLIENT"})
    @GetMapping("/ussd")
    public ResponseEntity<?> getSimData(@RequestParam(name = "code") String ussdCode) {
        return service.simUssdCodeService(ussdCode);
    }

//    @Secured({"ROLE_CLIENT"})
//    @GetMapping("/status")
//    public ResponseEntity<?> getActiveSimCards(@RequestParam(name = "q", defaultValue = "active") String active) {
//        return null;
//    }

    @Secured({"ROLE_MANAGER", "ROLE_STAFF"})
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return service.getAll(page, size);
    }

    @Secured({"ROLE_MANAGER", "ROLE_STAFF", "ROLE_CLIENT"})
    @GetMapping("/{simCardId}")
    public ResponseEntity<?> getOne(@PathVariable(name = "simCardId") UUID id) {
        return service.getOne(id);
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/change-tariff/{tariffId}")
    public ResponseEntity<?> changeTariff(@PathVariable(name = "tariffId") Long tariffId) {
        return service.changeTariff(tariffId);
    }

    @Secured("ROLE_MANAGER")
    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Valid SimCardDTO dto) {
        return service.addSimCard(dto);
    }
}
