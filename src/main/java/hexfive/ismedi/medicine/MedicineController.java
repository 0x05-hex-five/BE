package hexfive.ismedi.medicine;

import hexfive.ismedi.global.APIResponse;
import hexfive.ismedi.medicine.dto.ResMedicineDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medicine")
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping("/init")
    public void mergeMedicineData() {
        medicineService.mergeToMedicineTable();
    }

    @GetMapping("")
    public APIResponse<List<ResMedicineDto>> searchMedicines(
            @RequestParam(required = false, defaultValue = "") String name,    // 검색한 의약품명
            @RequestParam(defaultValue = "ALL") String type              // 전문/일반 의약품
    ) {
        MedicineType medicineType = MedicineType.from(type);
        return APIResponse.success(medicineService.getMedicines(name, medicineType));
    }

}
