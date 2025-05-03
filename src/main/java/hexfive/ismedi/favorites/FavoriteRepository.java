package hexfive.ismedi.favorites;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    boolean existsByUserIdAndMedicineId(Long userId, Long medicineId);
    Optional<Favorite> findByUserIdAndMedicineId(Long userId, Long medicineId);

}
