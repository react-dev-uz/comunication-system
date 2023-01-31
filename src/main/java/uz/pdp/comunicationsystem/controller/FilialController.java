package uz.pdp.comunicationsystem.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.pdp.comunicationsystem.payload.request.filial.FilialDTO;
import uz.pdp.comunicationsystem.payload.request.filial.FilialWithOnlyUserId;
import uz.pdp.comunicationsystem.service.FilialService;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/filial")
public class FilialController {
    private final FilialService service;

    @Autowired
    public FilialController(FilialService service) {
        this.service = service;
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_MANAGER"})
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "sort", defaultValue = "id") String sort) {
        return service.getAllFilial(sort);
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_MANAGER"})
    @GetMapping("/{filialId}")
    public ResponseEntity<?> getOne(@PathVariable(name = "filialId") Long id) {
        return service.getFilial(id);
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_MANAGER"})
    @PostMapping("/fulling")
    public ResponseEntity<?> save(@Valid @RequestBody FilialDTO filialDTO) {
        return service.addNewFilialWithDirector(filialDTO);
    }

    // Hodim sifatida mavjud bo'lgan hodimni Yangi filialga director qilish
    @Secured("ROLE_DIRECTOR")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody FilialWithOnlyUserId filialWithOnlyUserId) {
        return service.addNewFilialWithExistUser(filialWithOnlyUserId);
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_MANAGER"})
    @PutMapping("/{filialId}/owner")
    public ResponseEntity<?> addNewOwnerInFilial(@PathVariable(name = "filialId") Long filialId, @Valid @RequestBody Set<UUID> ownersId) {
        return service.addNewOwners(filialId, ownersId);
    }

    //    Filial tarkibidan hodimni o'chiriladi butunlay emas
    @Secured({"ROLE_DIRECTOR", "ROLE_MANAGER"})
    @PutMapping("/{filialId}/owner/{ownerId}")
    public ResponseEntity<?> deleteUserOnlyFilial(@PathVariable(name = "filialId") Long filialId, @PathVariable(name = "ownerId") UUID ownerId) {
        return service.deleteOwnerWithOnlyFilial(filialId, ownerId);
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_MANAGER"})
    @PutMapping("/{filialId}")
    public ResponseEntity<?> update(@PathVariable(name = "filialId") Long id, @RequestBody FilialWithOnlyUserId filialWithOnlyUserId) {
        return service.editFilial(id, filialWithOnlyUserId);
    }

    @Secured({"ROLE_DIRECTOR", "ROLE_MANAGER"})
    @DeleteMapping("/{filialId}")
    public ResponseEntity<?> delete(@PathVariable(name = "filialId") Long id) {
        return service.deleteFilial(id);
    }
}
