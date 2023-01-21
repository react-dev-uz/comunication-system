package uz.pdp.comunicationsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.comunicationsystem.repository.RoleRepository;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    private final RoleRepository repository;

    @Autowired
    public RoleController(RoleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }
}
