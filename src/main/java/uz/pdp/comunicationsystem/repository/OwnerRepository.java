package uz.pdp.comunicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.comunicationsystem.entity.Owner;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {
    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, UUID id);

    Optional<Owner> findByUsername(String username);
}