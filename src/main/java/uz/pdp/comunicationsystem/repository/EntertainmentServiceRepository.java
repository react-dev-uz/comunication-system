package uz.pdp.comunicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.comunicationsystem.entity.Entertainment;

@Repository
public interface EntertainmentServiceRepository extends JpaRepository<Entertainment, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}