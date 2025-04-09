package hexfive.ismedi.openApi;

import hexfive.ismedi.domain.PrescriptionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionTypeRepository extends JpaRepository<PrescriptionType, Long> {
}