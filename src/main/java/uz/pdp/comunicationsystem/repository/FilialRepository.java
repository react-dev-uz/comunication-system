package uz.pdp.comunicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.comunicationsystem.entity.Filial;

@Repository
public interface FilialRepository extends JpaRepository<Filial, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}