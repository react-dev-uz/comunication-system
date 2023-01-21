package uz.pdp.comunicationsystem.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.payload.request.OwnerDTO;
import uz.pdp.comunicationsystem.service.OwnerService;

import java.util.UUID;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {
    private final OwnerService service;

    @Autowired
    public OwnerController(OwnerService service) {
        this.service = service;
    }

    @Secured({"ROLE_ADMIN", "ROLE_STAFF"})
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "sort", defaultValue = "id") String sort, @RequestParam(name = "filter", defaultValue = "desc") String filter) {
        return service.getOwners(sort, filter);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STAFF"})
    @GetMapping("/{ownerId}")
    public ResponseEntity<?> getOne(@PathVariable(name = "ownerId") UUID ownerId) {
        return service.getOwner(ownerId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        return service.getMe();
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @PostMapping
    public ResponseEntity<?> saveOwner(@Valid @RequestBody OwnerDTO ownerDTO) {
        return service.addOwner(ownerDTO);
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_STAFF"})
    @PutMapping("/{ownerId}")
    public ResponseEntity<?> update(@PathVariable(name = "ownerId") UUID ownerId, @Valid @RequestBody OwnerDTO ownerDTO) {
        return service.editOwner(ownerId, ownerDTO);
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<?> delete(@PathVariable(name = "ownerId") UUID ownerId) {
        return service.deleteOwner(ownerId);
    }
}
