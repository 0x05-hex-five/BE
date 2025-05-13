package hexfive.ismedi.openApi;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfo;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionType;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfoRepository;
import hexfive.ismedi.openApi.dto.OpenAPIResponse;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hexfive.ismedi.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAPIService {
    private final DrugInfoRepository drugInfoRepository;
    private final PrescriptionTypeRepository prescriptionTypeRepository;
    private final ObjectMapper objectMapper;

    @Value("${api.key}")
    private String serviceKey;
    private final int MAX_ROW_CNT = 100;

    // 전체 데이터 수집
    public void fetchAll(APIType apiType) throws Exception {
        int pageNo = 1;
        int totalCount;
        int totalSaved = 0;
        int totalSkipped = 0;

        do {
            PageResult result = switch (apiType) {
                case DRUG_INFO -> fetchDrugInfoPage(apiType, pageNo);
                case PRESCRIPTION_TYPE -> fetchPrescriptionTypePage(apiType, pageNo);
                default -> throw new IllegalStateException("지원하지 않는 API type입니다: " + apiType);
            };

            totalCount = result.totalCount();
            totalSaved += result.saved();
            totalSkipped += result.skipped();

            pageNo++;
        } while ((pageNo - 1) * MAX_ROW_CNT < totalCount);

        log.info("저장 완료 - 총 저장: {}, 총 스킵: {}", totalSaved, totalSkipped);

        if (totalSaved + totalSkipped != totalCount) {
            throw new CustomException(MISMATCH_COUNT, totalSaved, totalSkipped, totalCount);
        }
    }

    // 페이지별 수집 - DRUG_INFO
    public PageResult fetchDrugInfoPage(APIType apiType, int pageNo) throws Exception {
        OpenAPIResponse<DrugInfo> response = fetch(apiType, pageNo, null);
        List<DrugInfo> items = response.getBody().getItems();

        List<DrugInfo> toSave = items.stream()
                .filter(item -> !drugInfoRepository.existsById(item.getItemSeq()))
                .collect(Collectors.toList());

        drugInfoRepository.saveAll(toSave);
        log.info("[{}페이지] 저장: {} / 스킵: {}", pageNo, toSave.size(), items.size() - toSave.size());
        return new PageResult(toSave.size(), items.size() - toSave.size(), response.getBody().getTotalCount());
    }

    // 페이지별 수집 - PRESCRIPTION_TYPE
    public PageResult fetchPrescriptionTypePage(APIType apiType, int pageNo) throws Exception {
        OpenAPIResponse<PrescriptionType> response = fetch(apiType, pageNo, null);
        List<PrescriptionType> items = response.getBody().getItems();

        List<PrescriptionType> toSave = items.stream()
                .filter(item -> !prescriptionTypeRepository.existsById(item.getItemSeq()))
                .collect(Collectors.toList());

        prescriptionTypeRepository.saveAll(toSave);
        log.info("[{}페이지] 저장: {} / 스킵: {}", pageNo, toSave.size(), items.size() - toSave.size());
        return new PageResult(toSave.size(), items.size() - toSave.size(), response.getBody().getTotalCount());
    }

    // 공통 호출 로직
    public <T> OpenAPIResponse<T> fetch(APIType apiType, int pageNo, Map<String, String> params) throws Exception {
        String apiUrl = apiType.getUrl();
        String type = "json";
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(apiUrl)
                .append("?serviceKey=").append(serviceKey)
                .append("&pageNo=").append(pageNo)
                .append("&numOfRows=").append(MAX_ROW_CNT)
                .append("&type=").append(type);

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }

        URI uri = new URI(uriBuilder.toString());
//        log.info("uri: {}", uri);

        RestTemplate template = new RestTemplate();
        String jsonResponse = template.getForObject(uri, String.class);

        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(OpenAPIResponse.class, apiType.getEntity());

//        log.info("{}", jsonResponse);
        return objectMapper.readValue(jsonResponse, javaType);
    }
}
