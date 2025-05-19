package hexfive.ismedi.openApi.data.xml;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XMLDrugInfoRepository extends JpaRepository<XmlDrugInfo, Long> {
    boolean existsByItemSeq(String seq);

    List<XmlDrugInfo> findAllByItemNameContaining(String name);
    List<XmlDrugInfo> findAllByEtcOtcCode(String type);
    List<XmlDrugInfo> findAllByItemNameContainingAndEtcOtcCode(String name, String type);
}
