package uz.pdp.comunicationsystem.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<?> getAllClients(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "order", defaultValue = "desc") String order
    ) {
        return service.getAllClients(page, size, sort, order);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOneClient(@PathVariable("id") UUID clientId) {
        return service.getOneClient(clientId);
    }

    @PostMapping
    public ResponseEntity<?> saveClient(@RequestBody @Valid ClientDTO clientDTO) {
        return service.addClientAndSimCard(clientDTO);
    }

    @GetMapping("/types")
    public ResponseEntity<?> getAllClientTypes() {
        return service.getClientType();
    }

    @PostMapping("/add-sim-card")
    public ResponseEntity<?> addSimCard(@RequestBody @Valid NewSimCardDto simCardDto) {
        return service.addSimCardForClient(simCardDto);
    }

    @PostMapping("/sms")
    public ResponseEntity<?> sendSms(@RequestBody @Valid SmsDTO smsDTO) {
        return service.sendSMS(smsDTO);
    }

    @PostMapping("/call")
    public ResponseEntity<?> call(@RequestBody @Valid CallDTO callDTO) {
        return service.call(callDTO);
    }
}
