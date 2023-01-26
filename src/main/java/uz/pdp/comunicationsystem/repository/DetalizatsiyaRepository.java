package uz.pdp.comunicationsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.comunicationsystem.entity.Detalizatsiya;

@Repository
public interface DetalizatsiyaRepository extends JpaRepository<Detalizatsiya, Integer> {
}