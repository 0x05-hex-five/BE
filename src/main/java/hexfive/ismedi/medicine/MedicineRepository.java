package hexfive.ismedi.medicine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findAllByItemNameContaining(String name);
    List<Medicine> findAllByEtcOtcCodeName(String type);
    List<Medicine> findAllByItemNameContainingAndEtcOtcCodeName(String name, String type);
}
