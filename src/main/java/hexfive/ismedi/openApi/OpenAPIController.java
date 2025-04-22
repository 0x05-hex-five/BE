package hexfive.ismedi.openApi;

import hexfive.ismedi.global.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hexfive.ismedi.global.ErrorCode.INVALID_API_TYPE;

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
}
