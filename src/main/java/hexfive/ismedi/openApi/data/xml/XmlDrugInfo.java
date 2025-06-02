package hexfive.ismedi.openApi.data.xml;

import hexfive.ismedi.openApi.data.xml.dto.DocData;
import hexfive.ismedi.openApi.data.xml.dto.DrugItem;
import hexfive.ismedi.openApi.data.xml.dto.Paragraph;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class XmlDrugInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Unique
    private String itemSeq;         // 품목 기준코드
    private String itemName;        // 품목명
    private String entpName;        // 업체명
    private String etcOtcCode;      // 전문일반

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String chart;           // 성상

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String materialName;    // 원료 성분

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String eeDocText;       // 효능효과

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String udDocText;       // 용법용량

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String nbDocText;       // 주의사항

    public static XmlDrugInfo from(DrugItem item) {
        XmlDrugInfo info = new XmlDrugInfo();
        info.itemSeq = item.getItemSeq();
        info.itemName = item.getItemName();
        info.entpName = item.getEntpName();
        info.etcOtcCode = item.getEtcOtcCode();
        info.chart = item.getChart();
        info.materialName = item.getMaterialName();
        info.eeDocText = extractText(item.getEeDocData());
        info.udDocText = extractText(item.getUdDocData());
        info.nbDocText = extractText(item.getNbDocData());
        return info;
    }

    private static String extractText(DocData docData) {
        if (docData == null ||
                docData.getDoc() == null ||
                docData.getDoc().getSection() == null ||
                docData.getDoc().getSection().getArticles() == null ||
                docData.getDoc().getSection().getArticles().isEmpty() ||
                docData.getDoc().getSection().getArticles().getFirst().getParagraphs() == null) {
            return "";
        }

        return docData.getDoc().getSection().getArticles().getFirst().getParagraphs().stream()
                .map(Paragraph::getText)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
    }
}
