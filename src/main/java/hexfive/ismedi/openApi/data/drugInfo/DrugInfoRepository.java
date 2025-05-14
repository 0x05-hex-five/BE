package hexfive.ismedi.openApi.data.drugInfo;

import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugInfoRepository extends JpaRepository<DrugInfo, String> {
    boolean existsByItemSeq(String itemSeq);
    Optional<DrugInfo> findByItemSeq(String itemSeq);
}
