package hexfive.ismedi.openApi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fetch")
public class DrugInfoController {
    private final OpenApiService openApiService;

    @RequestMapping("/init")
    public void fetchAll() throws Exception {
        openApiService.fetchAll(ApiType.DRUG_INFO);
        openApiService.fetchAll2(ApiType.PRESCRIPTION_TYPE);
    }

    @RequestMapping("/{type}/{pageNo}")
    public void fetchPage(@PathVariable String type, @PathVariable int pageNo) throws Exception {
        ApiType apiType = ApiType.from(type); // 문자열 -> enum 변환
        if (apiType == ApiType.DRUG_INFO)
            openApiService.fetchPage(apiType, pageNo);
        else if (apiType == ApiType.PRESCRIPTION_TYPE)
            openApiService.fetchPage2(apiType, pageNo);
    }
}
