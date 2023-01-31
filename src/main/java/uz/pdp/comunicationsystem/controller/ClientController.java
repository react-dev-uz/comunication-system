package uz.pdp.comunicationsystem.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.payload.response.CallDTO;
import uz.pdp.comunicationsystem.payload.response.ClientDTO;
import uz.pdp.comunicationsystem.payload.response.NewSimCardDto;
import uz.pdp.comunicationsystem.payload.response.SmsDTO;
import uz.pdp.comunicationsystem.service.ClientService;

import java.util.UUID;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final ClientService service;

    @Autowired
    public ClientController(ClientService service) {
        this.service = service;
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_MANAGER", "ROLE_STAFF"})
    @GetMapping
    public ResponseEntity<?> getAllClients(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "order", defaultValue = "desc") String order
    ) {
        return service.getAllClients(page, size, sort, order);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOneClient(@PathVariable("id") UUID clientId) {
        return service.getOneClient(clientId);
    }

    @Secured({"ROLE_STAFF"})
    @PostMapping
    public ResponseEntity<?> saveClient(@RequestBody @Valid ClientDTO clientDTO) {
        return service.addClientAndSimCard(clientDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/types")
    public ResponseEntity<?> getAllClientTypes() {
        return service.getClientType();
    }

    @Secured({"ROLE_STAFF"})
    @PostMapping("/add-sim-card")
    public ResponseEntity<?> addSimCard(@RequestBody @Valid NewSimCardDto simCardDto) {
        return service.addSimCardForClient(simCardDto);
    }

    @Secured({"ROLE_CLIENT"})
    @PostMapping("/sms")
    public ResponseEntity<?> sendSms(@RequestBody @Valid SmsDTO smsDTO) {
        return service.sendSMS(smsDTO);
    }

    @Secured({"ROLE_CLIENT"})
    @PostMapping("/call")
    public ResponseEntity<?> call(@RequestBody @Valid CallDTO callDTO) {
        return service.call(callDTO);
    }
}
