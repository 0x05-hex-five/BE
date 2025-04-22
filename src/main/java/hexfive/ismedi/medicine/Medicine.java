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

    // DrugInfo
    private String entpName; // 업체명
    @Column(length = 1000)
    private String itemName; // 제품명
    @Column(unique = true)
    private String itemSeq; // 품목기준코드
    @Column(length = 2000)
    private String efcyQesitm; // 효능
    @Column(length = 2000)
    private String useMethodQesitm; // 사용법
    @Column(length = 2000)
    private String atpnQesitm; // 주의사항
    @Column(length = 2000)
    private String intrcQesitm; // 상호작용
    @Column(length = 2000)
    private String seQesitm; // 부작용
    @Column(length = 2000)
    private String depositMethodQesitm; // 보관법
    private String itemImage; // 낱알이미지

    // PrescriptionType
    private String etcOtcCodeName; // 의약품 분류(전문/일반)
    private String classNoName; // 약 정보

    // @ManyToOne
    // @JoinColumn(name = "category_id", nullable = true)
    // private Category category;
}