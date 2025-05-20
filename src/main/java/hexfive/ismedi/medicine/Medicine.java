package hexfive.ismedi.medicine;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "Medicine")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // XMLDrugInfo
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


    private String itemImage; // 낱알이미지

    private String classNoName; // 약 정보
}