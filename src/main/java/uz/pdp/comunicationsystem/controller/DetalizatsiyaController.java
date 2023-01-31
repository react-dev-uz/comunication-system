package uz.pdp.comunicationsystem.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.payload.response.DetailsDTO;
import uz.pdp.comunicationsystem.service.DetalizatsiyaService;

import java.util.UUID;

@RestController
@RequestMapping("/api/details")
public class DetalizatsiyaController {
    private final DetalizatsiyaService service;

    @Autowired
    public DetalizatsiyaController(DetalizatsiyaService service) {
        this.service = service;
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_MANAGER"})
    @GetMapping
    public ResponseEntity<?> getAllDetails(@RequestParam(name = "page", defaultValue = "1") Integer currentPage, @RequestParam(name = "size", defaultValue = "20") Integer pageSize, @RequestParam(name = "sort", defaultValue = "createdAt") String sort) {
        return service.getAllDetails(currentPage, pageSize, sort);
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_CLIENT"})
    @GetMapping("/{simCardId}")
    public ResponseEntity<?> getOneSimCardDetails(@PathVariable("simCardId") UUID simCardId) {
        return service.getOneSimCardDetails(simCardId);
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_CLIENT"})
    @GetMapping("/actions")
    public ResponseEntity<?> getAllAction() {
        return service.getAllActions();
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_CLIENT"})
    @GetMapping("/{simCardId}/export")
    public ResponseEntity<?> export(@PathVariable("simCardId") UUID simCardId, @RequestParam(name = "type", defaultValue = "excel") String type) {
        return service.exportDetailsToDocumentFormat(simCardId, type);
    }

    @PostMapping
    public ResponseEntity<?> addDetails(@Valid @RequestBody DetailsDTO detailsDTO) {
        return service.addNewAction(detailsDTO);
    }
}
