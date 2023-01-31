package uz.pdp.comunicationsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.payload.request.PacketDTO;
import uz.pdp.comunicationsystem.payload.response.PacketType;
import uz.pdp.comunicationsystem.service.PacketService;

import java.util.Set;

@RestController
@RequestMapping("/api/packet")
public class PacketController {
    private final PacketService service;

    @Autowired
    public PacketController(PacketService service) {
        this.service = service;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "sort", defaultValue = "id") String sort) {
        return service.packets(sort);
    }

    @Secured({"ROLE_MANAGER", "ROLE_DIRECTOR", "ROLE_CLIENT"})
    @GetMapping(value = "/{packetId}")
    public ResponseEntity<?> getOne(@PathVariable(name = "packetId") Long id) {
        return service.packet(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/types")
    public ResponseEntity<Set<PacketType>> getTypes() {
        return service.packetTypes();
    }

    @Secured("ROLE_MANAGER")
    @PostMapping
    public ResponseEntity<?> save(@RequestBody PacketDTO packetDTO) {
        return service.addPacket(packetDTO);
    }

    @Secured("ROLE_MANAGER")
    @PutMapping("/{packetId}")
    public ResponseEntity<?> update(@PathVariable(name = "packetId") Long packetId, @RequestBody PacketDTO packetDTO) {
        return service.editPacket(packetId, packetDTO);
    }

    @Secured("ROLE_MANAGER")
    @DeleteMapping("/{packetId}")
    public ResponseEntity<?> delete(@PathVariable(name = "packetId") Long packetId) {
        return service.deletePacket(packetId);
    }
}
