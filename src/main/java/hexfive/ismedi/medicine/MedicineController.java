package hexfive.ismedi.medicine;

import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.medicine.dto.ResInteractionDto;
import hexfive.ismedi.global.swagger.MedicineDocs;
import hexfive.ismedi.medicine.dto.ResMedicineDetailDto;
import hexfive.ismedi.medicine.dto.ResMedicineDto;
import hexfive.ismedi.openApi.APIType;
import hexfive.ismedi.openApi.OpenAPIService;
import hexfive.ismedi.openApi.data.xml.XmlDrugInfo;
import hexfive.ismedi.openApi.data.xml.dto.XMLAPIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/medicines")
public class MedicineController implements MedicineDocs {

    private final MedicineService medicineService;
    private final OpenAPIService openAPIService;

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

    @GetMapping("/{id}")
    public APIResponse<ResMedicineDetailDto> searchMedicines(@PathVariable Long id){
        return APIResponse.success(medicineService.getMedicine(id));
    }

    @GetMapping("/interactions")
    public APIResponse<ResInteractionDto> checkInteraction(
            @RequestParam Long id1,
            @RequestParam Long id2
    ) throws Exception {
        return APIResponse.success(medicineService.checkInteraction(id1, id2));
    }
}
