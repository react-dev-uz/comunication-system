package uz.pdp.comunicationsystem.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.comunicationsystem.entity.Filial;
import uz.pdp.comunicationsystem.entity.Owner;
import uz.pdp.comunicationsystem.entity.Role;
import uz.pdp.comunicationsystem.entity.enums.RoleName;
import uz.pdp.comunicationsystem.payload.request.filial.DirectorDTO;
import uz.pdp.comunicationsystem.payload.request.filial.FilialDTO;
import uz.pdp.comunicationsystem.payload.request.filial.FilialWithOnlyUserId;
import uz.pdp.comunicationsystem.repository.FilialRepository;
import uz.pdp.comunicationsystem.repository.OwnerRepository;
import uz.pdp.comunicationsystem.repository.RoleRepository;

import java.util.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;

@Service
@Valid
public class FilialService {
    private final FilialRepository repository;
    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public FilialService(FilialRepository repository, OwnerRepository ownerRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.ownerRepository = ownerRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<?> getAllFilial(String sort) {
        return ok(repository.findAll(Sort.by(sort)));
    }

    public ResponseEntity<?> getFilial(Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    @Transactional
    public ResponseEntity<?> addNewFilialWithDirector(FilialDTO dto) {
        if (repository.existsByName(dto.getName()))
            return status(UNPROCESSABLE_ENTITY).body("Ushbu filial nomi oldin ro'yhatga olingan.");
        DirectorDTO director = dto.getDirector();
        if (ownerRepository.existsByUsername(director.getUsername()))
            return status(UNPROCESSABLE_ENTITY).body("Ushbu foydalanuvchi nomi oldin ro'yhatga olingan.");
        return roleRepository.findByName(RoleName.ROLE_DIRECTOR)
                .map(role -> {
                    Owner owner = new Owner(director.getUsername(), director.getFirstname(), director.getLastname(), passwordEncoder.encode(director.getPassword()), true, Set.of(role));
                    owner = ownerRepository.save(owner);
                    try {
                        return status(CREATED).body(repository.save(new Filial(dto.getName(), owner, new LinkedList<>())));
                    } catch (Exception e) {
                        return badRequest().body("OOPS :(, nimadir xato iltimos ma'lumotlarni tekshirib qayta urunib ko'ring");
                    }
                })
                .orElseGet(() -> status(INTERNAL_SERVER_ERROR).body("Majburiy ROLE_DIRECTOR type topilmadi"));
    }

    @Transactional
    public ResponseEntity<?> addNewFilialWithExistUser(FilialWithOnlyUserId dto) {
        if (repository.existsByName(dto.getName()))
            return status(UNPROCESSABLE_ENTITY).body("Filial nomi oldin ro'yhatga olingan");
        return ownerRepository.findById(dto.getDirectorId()).map(owner -> {
            if (!owner.isEnabled()) owner.setEnabled(true);  // yangi direktorni aktivlashtirib qo'yamiz
            Optional<Role> optionalRole = roleRepository.findByName(RoleName.ROLE_DIRECTOR);
            if (optionalRole.isEmpty())
                return status(INTERNAL_SERVER_ERROR).body("Serverdagi xatolik Majburiy ROLE qiymati topilmadi");
            owner.setRoles(Set.of(optionalRole.get()));
            return status(CREATED).body(repository.save(new Filial(dto.getName(), owner, List.of())));
        }).orElseGet(() -> notFound().build());
    }

    @Transactional
    public ResponseEntity<?> editFilial(Long filialId, FilialWithOnlyUserId dto) {
        return repository.findById(filialId).map(filial -> {
            if (!repository.existsByNameAndIdNot(dto.getName(), filialId)) filial.setName(dto.getName());
            Owner filialDirector = filial.getDirector();
            if (filialDirector.getId() != dto.getDirectorId()) {
                return ownerRepository.findById(dto.getDirectorId()).map(owner -> {
                            Optional<Role> optionalRole = roleRepository.findByName(RoleName.ROLE_DIRECTOR);
                            if (optionalRole.isEmpty())
                                return status(INTERNAL_SERVER_ERROR).body("Serverdagi xatolik Majburiy ROLE qiymati topilmadi");
                            owner.setRoles(Set.of(optionalRole.get()));
                            owner.setFilial(filial);
                            owner = ownerRepository.save(owner);
                            filial.setDirector(owner);
                            return status(CREATED).body(repository.save(filial));
                        })
                        .orElseGet(() -> status(NOT_FOUND).body("Afsuski bu id bilan bizning bazamizda hodim yo'q"));
            }
            return ok(repository.save(filial));
        }).orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> deleteFilial(Long id) {
        return repository.findById(id).map(filial -> {
            try {
                repository.delete(filial);
                return noContent().build();
            } catch (Exception e) {
                return badRequest().body("Afsuski ushbu filial o'chirilmadi chunki unga bog'lagan ma'lumotlar bo'lishi mumkin ularni o'zgartiring yoki o'chirib qayta urunib ko'ring");
            }
        }).orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> addNewOwners(Long filialId, Set<UUID> ownersId) {
        if (ownersId.size() == 0)
            return badRequest().body("Hodimlar idlari ro'yhati bo'sh. Iltimos tekshirib qayta urunib ko'ring");
        Set<Owner> owners = new HashSet<>();
        return repository.findById(filialId)
                .map(filial -> {
                    ownersId.forEach(uuid -> ownerRepository.findById(uuid).ifPresent(owners::add));
                    owners.addAll(filial.getOwners());
                    filial.setOwners(owners.stream().toList());
                    return status(CREATED).body(repository.save(filial));
                })
                .orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> deleteOwnerWithOnlyFilial(Long filialId, UUID ownerId) {
        return repository.findById(filialId)
                .map(filial -> ownerRepository.findById(ownerId)
                        .map(owner -> {
                            List<Owner> filialOwners = filial.getOwners();
                            boolean remove = filialOwners.remove(owner);
                            filial.setOwners(filialOwners);
                            return remove ? ok("Hodim muvaffaqiyatli filialdan olib tashlandi") : badRequest().body("OOPS. Nimadir xato ketdi");
                        })
                        .orElseGet(() -> status(NOT_FOUND).body("Hodim topilmadi")))
                .orElseGet(() -> notFound().build());
    }
}

