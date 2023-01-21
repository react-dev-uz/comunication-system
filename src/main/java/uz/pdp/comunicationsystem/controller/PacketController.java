package uz.pdp.comunicationsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.payload.response.PacketType;
import uz.pdp.comunicationsystem.payload.request.PacketDTO;
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

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "sort", defaultValue = "id") String sort) {
        return service.packets(sort);
    }

    @GetMapping(value = "/{packetId}")
    public ResponseEntity<?> getOne(@PathVariable(name = "packetId") Long id) {
        return service.packet(id);
    }

    @GetMapping("/types")
    public ResponseEntity<Set<PacketType>> getTypes() {
        return service.packetTypes();
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody PacketDTO packetDTO) {
        return service.addPacket(packetDTO);
    }

    @PutMapping("/{packetId}")
    public ResponseEntity<?> update(@PathVariable(name = "packetId") Long packetId, @RequestBody PacketDTO packetDTO) {
        return service.editPacket(packetId, packetDTO);
    }

    @DeleteMapping("/{packetId}")
    public ResponseEntity<?> delete(@PathVariable(name = "packetId") Long packetId) {
        return service.deletePacket(packetId);
    }
}
