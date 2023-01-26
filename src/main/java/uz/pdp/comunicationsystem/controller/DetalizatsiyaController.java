package uz.pdp.comunicationsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    public ResponseEntity<?> getAllDetails(@RequestParam(name = "page", defaultValue = "1") Integer currentPage, @RequestParam(name = "size", defaultValue = "20") Integer pageSize, @RequestParam(name = "sort", defaultValue = "created_at") String sort) {
        return service.getAllDetails(currentPage, pageSize, sort);
    }

    @GetMapping("/{simCardId}")
    public ResponseEntity<?> getOneSimCardDetails(@PathVariable("simCardId") UUID simCardId) {
        return null;
    }

    @GetMapping("/{simCardId}/export")
    public ResponseEntity<?> export(@PathVariable("simCardId") UUID simCardId, @RequestParam(name = "type", defaultValue = "excel") String type) {
        return null;
    }
}
