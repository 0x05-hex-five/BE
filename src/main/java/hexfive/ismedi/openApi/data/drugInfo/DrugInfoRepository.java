package hexfive.ismedi.openApi.data.drugInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugInfoRepository extends JpaRepository<DrugInfo, String> {
    boolean existsByItemSeq(String itemSeq);
}
