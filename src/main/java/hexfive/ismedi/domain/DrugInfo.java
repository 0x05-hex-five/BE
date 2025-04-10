package hexfive.ismedi.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 식품의약품안전처_의약품개요정보(e약은요)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DrugInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entpName;
    @Column(length = 1000)
    private String itemName;
    private String itemSeq;
    @Column(length = 2000)
    private String efcyQesitm;
    @Column(length = 2000)
    private String useMethodQesitm;
    @Column(length = 2000)
    private String atpnQesitm;
    @Column(length = 2000)
    private String intrcQesitm;
    @Column(length = 2000)
    private String seQesitm;
    @Column(length = 2000)
    private String depositMethodQesitm;
    private String itemImage;
}