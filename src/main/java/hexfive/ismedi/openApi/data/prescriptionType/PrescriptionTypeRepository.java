package hexfive.ismedi.openApi.data.prescriptionType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrescriptionTypeRepository extends JpaRepository<PrescriptionType, Long> {
    Optional<PrescriptionType> findByItemSeq(String itemSeq);
    boolean existsByItemSeq(String itemSeq);
}