package hexfive.ismedi.openApi;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexfive.ismedi.domain.DrugInfo;
import hexfive.ismedi.domain.PrescriptionType;
import hexfive.ismedi.openApi.dto.DrugInfoDto;
import hexfive.ismedi.openApi.dto.LocalDataResponse;
import hexfive.ismedi.openApi.dto.PrescriptionTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrugInfoService {
    private final DrugInfoRepository drugInfoRepository;
    private final PrescriptionTypeRepository prescriptionTypeRepository;
    private final ObjectMapper objectMapper;

    @Value("${api.key}")
    private String serviceKey;
    private int MAX_ROW_CNT = 100;

    // 전체 데이터 수집 - DRUG_INFO
    public void fetchAll(ApiType apiType) throws Exception {
        int pageNo = 1;
        int totalCount;
        int totalSaved = 0;

        do {
            LocalDataResponse<DrugInfoDto> response = fetch(apiType, pageNo);
            totalCount = response.getBody().getTotalCount();
            List<DrugInfoDto> items = response.getBody().getItems();

            List<DrugInfo> entities = items.stream()
                    .map(DrugInfoDto::toEntity)
                    .collect(Collectors.toList());

            drugInfoRepository.saveAll(entities);
            totalSaved += entities.size();
            log.info("[{}페이지] 전체 저장 개수: {}", pageNo, totalSaved);

            pageNo++;
        } while ((pageNo - 1) * MAX_ROW_CNT < totalCount);

        if (totalSaved != totalCount) {
            throw new IllegalStateException(String.format("저장된 건수 : %d / 전체 건수 : %d - 불일치", totalSaved, totalCount));
        }
    }

    // 페이지별 수집 - DRUG_INFO
    public void fetchPage(ApiType apitype, int pageNo) throws Exception {
        LocalDataResponse<DrugInfoDto> response = fetch(apitype, pageNo);
        List<DrugInfoDto> items = response.getBody().getItems();

        List<DrugInfo> entities = items.stream()
                .map(DrugInfoDto::toEntity)
                .collect(Collectors.toList());

        drugInfoRepository.saveAll(entities);
        log.info("[{}페이지] 저장한 약 정보 수: {}", pageNo, entities.size());
    }

    // 전체 데이터 수집 - PRESCRIPTION_TYPE
    public void fetchAll2(ApiType apiType) throws Exception {
        int pageNo = 1;
        int totalCount;
        int totalSaved = 0;

        do {
            LocalDataResponse<PrescriptionTypeDto> response = fetch(apiType, pageNo);
            totalCount = response.getBody().getTotalCount();
            List<PrescriptionTypeDto> items = response.getBody().getItems();

            List<PrescriptionType> entities = items.stream()
                    .map(PrescriptionTypeDto::toEntity)
                    .collect(Collectors.toList());

            prescriptionTypeRepository.saveAll(entities);
            totalSaved += entities.size();
            log.info("[{}페이지] 누적 저장: {}", pageNo, totalSaved);

            pageNo++;
        } while ((pageNo - 1) * MAX_ROW_CNT < totalCount);

        if (totalSaved != totalCount) {
            throw new IllegalStateException(String.format(
                    "저장된 건수 : %d / 전체 건수 : %d - 불일치", totalSaved, totalCount
            ));
        }
    }

    // 페이지별 수집 - PRESCRIPTION_TYPE
    public void fetchPage2(ApiType apiType, int pageNo) throws Exception {
        LocalDataResponse<PrescriptionTypeDto> response = fetch(apiType, pageNo);
        List<PrescriptionTypeDto> items = response.getBody().getItems();

        List<PrescriptionType> entities = items.stream()
                .map(PrescriptionTypeDto::toEntity)
                .collect(Collectors.toList());

        prescriptionTypeRepository.saveAll(entities);
        log.info("[{}페이지] 저장 완료: {}건", pageNo, entities.size());
    }

    // 공통 호출 로직
    private <T> LocalDataResponse<T> fetch(ApiType apiType, int pageNo) throws Exception {
        String apiUrl = apiType.getUrl();
        String type = "json";
        String uriStr = String.format("%s?serviceKey=%s&pageNo=%d&numOfRows=%d&type=%s",
                apiUrl, serviceKey, pageNo, MAX_ROW_CNT, type);

        URI uri = new URI(uriStr);
        log.info("uri: {}", uri);

        RestTemplate template = new RestTemplate();
        String jsonResponse = template.getForObject(uri, String.class);

        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(LocalDataResponse.class, apiType.getDtoClass());

        return objectMapper.readValue(jsonResponse, javaType);
    }
}
