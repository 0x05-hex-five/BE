package hexfive.ismedi.openApi;

import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.medicine.MedicineType;
import hexfive.ismedi.medicine.dto.ResMedicineDto;
import hexfive.ismedi.openApi.data.xml.XmlDrugInfo;
import hexfive.ismedi.openApi.data.xml.dto.XMLAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static hexfive.ismedi.global.exception.ErrorCode.INVALID_API_TYPE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fetch")
public class OpenAPIController {
    private final OpenAPIService openAPIService;

    @RequestMapping("/init")
    public void fetchAll() throws Exception {
        openAPIService.fetchAll(APIType.DRUG_INFO);
        openAPIService.fetchAll(APIType.PRESCRIPTION_TYPE);
    }

    @RequestMapping("/{type}/{pageNo}")
    public void fetchPage(@PathVariable String type, @PathVariable int pageNo) throws Exception {
        APIType apiType = APIType.from(type); // 문자열 -> enum 변환
        switch (apiType) {
            case DRUG_INFO -> openAPIService.fetchDrugInfoPage(apiType, pageNo);
            case PRESCRIPTION_TYPE -> openAPIService.fetchPrescriptionTypePage(apiType, pageNo);
            default -> throw new CustomException(INVALID_API_TYPE, type);
        }
    }

    @GetMapping("/xml")
    public void test() {
        openAPIService.fetchXMLAll(APIType.XML);
    }

    @GetMapping("/test")
    public APIResponse<List<XmlDrugInfo>> searchNewMedicines(
            @RequestParam(required = false, defaultValue = "") String name,    // 검색한 의약품명
            @RequestParam(defaultValue = "ALL") String type              // 전문/일반 의약품
    ) {
        MedicineType medicineType = MedicineType.from(type);
        return APIResponse.success(openAPIService.getNewMedicines(name, medicineType));
    }
}
