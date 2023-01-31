package uz.pdp.comunicationsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.comunicationsystem.entity.Filial;
import uz.pdp.comunicationsystem.entity.Owner;
import uz.pdp.comunicationsystem.payload.request.OwnerDTO;
import uz.pdp.comunicationsystem.repository.FilialRepository;
import uz.pdp.comunicationsystem.repository.OwnerRepository;
import uz.pdp.comunicationsystem.repository.RoleRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.*;

@Service
public class OwnerService {
    private final OwnerRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FilialRepository filialRepository;

    @Autowired
    public OwnerService(OwnerRepository repository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                        FilialRepository filialRepository) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.filialRepository = filialRepository;
    }

    public ResponseEntity<?> getOwners(String sort, String filter) {
        return ResponseEntity.ok(repository.findAll(Sort.by(sort, filter)));
    }

    public ResponseEntity<?> getOwner(UUID ownerId) {
        return repository.findById(ownerId).map(ResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> getMe() {
        Owner principal = (Owner) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return repository.findById(principal.getId()).map(ResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> addOwner(OwnerDTO dto) {
        if (repository.existsByUsername(dto.getUsername()))
            return status(422).body("Ushbu foydalanuvchi nomi oldin ro'yhatga olingan");
        Owner principal = (Owner) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Filial> optionalFilial = filialRepository.findById(dto.getFilialId());
        if (optionalFilial.isEmpty()) return status(404).body("filial not found");
        return roleRepository.findById(dto.getRoleId())
                .map(role -> {
                    Owner save = repository.save(new Owner(dto.getUsername(), dto.getFirstname(), dto.getLastname(), passwordEncoder.encode(dto.getPassword()), true, Set.of(role), optionalFilial.get()));
                    return status(201).body(save);
                })
                .orElseGet(() -> notFound().build());
    }

    @Transactional
    public ResponseEntity<?> deleteOwner(UUID ownerId) {
        return repository.findById(ownerId)
                .map(owner -> {
                    try {
                        repository.delete(owner);
                        return noContent().build();
                    } catch (Exception e) {
                        return badRequest().body("OOPS. Nimadir xato ketdi iltimos hodimga aloqador boshqa bog'liklar bor yo'qligini tekshirib qayta urunib ko'ring");
                    }
                }).orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> editOwner(UUID ownerId, OwnerDTO dto) {
        Optional<Owner> optionalOwner = repository.findById(ownerId);
        if (optionalOwner.isEmpty()) return status(NOT_FOUND).body("Owner not found");
        if (repository.existsByUsernameAndIdNot(dto.getUsername(), ownerId))
            return status(422).body("This username is already registered.");
        Optional<Filial> optionalFilial = filialRepository.findById(dto.getFilialId());
        if (optionalFilial.isEmpty()) return status(NOT_FOUND).body("Filial not found");
        Owner owner = optionalOwner.get();
        owner.setFirstName(dto.getFirstname());
        owner.setLastName(dto.getLastname());
        owner.setFilial(optionalFilial.get());
        owner.setPassword(passwordEncoder.encode(dto.getPassword()));
        owner.setUsername(dto.getUsername());
        repository.save(owner);
        return ok("Owner updated");
    }
}
