package hexfive.ismedi.openApi;

import hexfive.ismedi.domain.DrugInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugInfoRepository extends JpaRepository<DrugInfo, Long> {
    Optional<DrugInfo> findByItemSeq(String itemSeq);
}
