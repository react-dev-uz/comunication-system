package uz.pdp.comunicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.comunicationsystem.entity.Tariff;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}