package hexfive.ismedi.medicine;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medicine")
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping("/init")
    public void mergeMedicineData() {
        medicineService.mergeToMedicineTable();
    }

    @Entity
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Table(name = "Medicine")
    public static class Medicine {
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
}
