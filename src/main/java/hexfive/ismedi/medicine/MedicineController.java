package hexfive.ismedi.medicine;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping("/init")
    public void mergeMedicineData() {
        medicineService.mergeToMedicineTable();
    }
}
