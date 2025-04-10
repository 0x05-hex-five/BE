package hexfive.ismedi.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Medicine")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DrugInfo
    private String entpName;
    @Column(length = 1000)
    private String itemName;
    @Column(unique = true)
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

    // PrescriptionType
    private String etcOtcCodeName;
    private String classNoName;

//    @ManyToOne
//    @JoinColumn(name = "category_id", nullable = true)
//    private Category category;
}
