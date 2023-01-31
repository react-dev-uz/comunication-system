package uz.pdp.comunicationsystem.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.comunicationsystem.entity.Packet;

import java.util.Optional;

@Repository
public interface PacketRepository extends JpaRepository<Packet, Long> {
    boolean existsByName(String name);

    boolean existsByUssdCode_IdAndIdNot(Long ussdCode_id, Long id);

    Optional<Packet> findByUssdCode_Code(@NotNull String ussdCode_code);
}