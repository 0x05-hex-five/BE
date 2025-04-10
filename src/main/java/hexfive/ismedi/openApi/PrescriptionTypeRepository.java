package hexfive.ismedi.openApi;

import hexfive.ismedi.domain.DrugInfo;
import hexfive.ismedi.domain.PrescriptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrescriptionTypeRepository extends JpaRepository<PrescriptionType, Long> {
    Optional<PrescriptionType> findByItemSeq(String itemSeq);

}