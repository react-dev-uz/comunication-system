package uz.pdp.comunicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.comunicationsystem.entity.SimCard;

import java.util.Optional;
import java.util.UUID;

public interface SimCardRepository extends JpaRepository<SimCard, UUID> {
    Optional<SimCard> findByCodeAndNumber(String code, String number);
}