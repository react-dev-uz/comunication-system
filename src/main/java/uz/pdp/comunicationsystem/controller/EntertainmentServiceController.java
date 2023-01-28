package uz.pdp.comunicationsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.payload.response.EntertainmentServiceDTO;
import uz.pdp.comunicationsystem.service.EntertainmentService;

@RestController
@RequestMapping("/api/entertainment-service")
public class EntertainmentServiceController {
    private final EntertainmentService service;

    @Autowired
    public EntertainmentServiceController(EntertainmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getEntertainments() {
        return service.getAll();
    }

    @GetMapping("/{entertainmentId}")
    public ResponseEntity<?> getEntertainmentById(@PathVariable Long entertainmentId) {
        return service.getOne(entertainmentId);
    }

    @PostMapping
    public ResponseEntity<?> addEntertainment(EntertainmentServiceDTO entertainmentServiceDTO) {
        return service.add(entertainmentServiceDTO);
    }

    @PutMapping("/{entertainmentId}")
    public ResponseEntity<?> editEntertainment(@PathVariable Long entertainmentId, EntertainmentServiceDTO entertainmentServiceDTO) {
        return service.edit(entertainmentId, entertainmentServiceDTO);
    }
}
