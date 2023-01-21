package uz.pdp.comunicationsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<?> ussdCodes() {
        return service.getAll();
    }

    @GetMapping("/{ussdId}")
    public ResponseEntity<?> ussdCode(@PathVariable(name = "ussdId") Long id) {
        return service.getOne(id);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody UssdCode ussdCode) {
        return service.addOne(ussdCode);
    }

    @PutMapping("/{ussdId}")
    public ResponseEntity<?> update(@PathVariable("ussdId") Long id, @RequestBody UssdCode ussdCode) {
        return service.editOne(id, ussdCode);
    }

    @DeleteMapping("/{ussdId}")
    public ResponseEntity<?> delete(@PathVariable("ussdId") Long id) {
        return service.deleteOne(id);
    }
}
