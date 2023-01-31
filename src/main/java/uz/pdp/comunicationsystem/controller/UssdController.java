package uz.pdp.comunicationsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.entity.UssdCode;
import uz.pdp.comunicationsystem.service.UssdCodeService;

@RestController
@RequestMapping("/api/ussd-code")
public class UssdController {
    private final UssdCodeService service;

    @Autowired
    public UssdController(UssdCodeService service) {
        this.service = service;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> ussdCodes() {
        return service.getAll();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{ussdId}")
    public ResponseEntity<?> ussdCode(@PathVariable(name = "ussdId") Long id) {
        return service.getOne(id);
    }

    @Secured({"ROLE_MANAGER", "ROLE_DIRECTOR"})
    @PostMapping
    public ResponseEntity<?> save(@RequestBody UssdCode ussdCode) {
        return service.addOne(ussdCode);
    }

    @Secured({"ROLE_MANAGER", "ROLE_DIRECTOR"})
    @PutMapping("/{ussdId}")
    public ResponseEntity<?> update(@PathVariable("ussdId") Long id, @RequestBody UssdCode ussdCode) {
        return service.editOne(id, ussdCode);
    }

    @Secured({"ROLE_MANAGER", "ROLE_DIRECTOR"})
    @DeleteMapping("/{ussdId}")
    public ResponseEntity<?> delete(@PathVariable("ussdId") Long id) {
        return service.deleteOne(id);
    }
}
