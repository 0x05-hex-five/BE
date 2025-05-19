package hexfive.ismedi.openApi;

import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.openApi.data.xml.XmlDrugInfo;
import hexfive.ismedi.openApi.data.xml.dto.XMLAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        try {
            openAPIService.fetchXMLAll(APIType.XML);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
