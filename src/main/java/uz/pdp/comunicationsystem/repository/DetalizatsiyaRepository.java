package uz.pdp.comunicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.comunicationsystem.entity.Detalizatsiya;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DetalizatsiyaRepository extends JpaRepository<Detalizatsiya, Integer> {
    Optional<Detalizatsiya> findAllBySimCard_Id(UUID simCard_id);
}