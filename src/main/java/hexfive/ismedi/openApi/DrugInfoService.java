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
        int totalSkipped = 0;

        do {
            LocalDataResponse<DrugInfoDto> response = fetch(apiType, pageNo);
            totalCount = response.getBody().getTotalCount();
            List<DrugInfoDto> items = response.getBody().getItems();

            List<DrugInfo> toSave = items.stream()
                    .filter(dto -> {
                        boolean exists = drugInfoRepository.existsByItemSeq(dto.getItemSeq());
                        if (exists) {
                            log.info("[중복] itemSeq={} 저장 스킵", dto.getItemSeq());
                            return false;
                        }
                        return true;
                    })
                    .map(DrugInfoDto::toEntity)
                    .collect(Collectors.toList());

            drugInfoRepository.saveAll(toSave);
            totalSaved += toSave.size();
            totalSkipped += items.size() - toSave.size();
            log.info("[{}페이지] 저장: {}건 / 스킵: {}건 / 누적 저장: {}", pageNo, toSave.size(), items.size() - toSave.size(), totalSaved);

            pageNo++;
        } while ((pageNo - 1) * MAX_ROW_CNT < totalCount);

        log.info("저장 완료 - 총 저장: {}, 총 스킵: {}", totalSaved, totalSkipped);

        if (totalSaved + totalSkipped != totalCount) {
            throw new IllegalStateException(String.format(
                    "처리된 건수 불일치: 저장 %d + 스킵 %d ≠ 전체 %d", totalSaved, totalSkipped, totalCount
            ));
        }
    }

    // 페이지별 수집 - DRUG_INFO
    public void fetchPage(ApiType apiType, int pageNo) throws Exception {
        LocalDataResponse<DrugInfoDto> response = fetch(apiType, pageNo);
        List<DrugInfoDto> items = response.getBody().getItems();

        List<DrugInfo> toSave = items.stream()
                .filter(dto -> {
                    boolean exists = drugInfoRepository.existsByItemSeq(dto.getItemSeq());
                    if (exists) {
                        log.info("[중복] itemSeq={} 저장 스킵", dto.getItemSeq());
                        return false;
                    }
                    return true;
                })
                .map(DrugInfoDto::toEntity)
                .collect(Collectors.toList());

        drugInfoRepository.saveAll(toSave);
        log.info("[{}페이지] 저장 완료: {}건 / 스킵된 건수: {}", pageNo, toSave.size(), items.size() - toSave.size());
    }
    // 전체 데이터 수집 - PRESCRIPTION_TYPE
    public void fetchAll2(ApiType apiType) throws Exception {
        int pageNo = 1;
        int totalCount;
        int totalSaved = 0;
        int totalSkipped = 0;

        do {
            LocalDataResponse<PrescriptionTypeDto> response = fetch(apiType, pageNo);
            totalCount = response.getBody().getTotalCount();
            List<PrescriptionTypeDto> items = response.getBody().getItems();

            for (PrescriptionTypeDto dto : items) {
                try {
                    if (prescriptionTypeRepository.existsByItemSeq(dto.getItemSeq())) {
                        totalSkipped++;
                        log.info("[중복] itemSeq={} 저장 스킵", dto.getItemSeq());
                        continue;
                    }

                    PrescriptionType entity = dto.toEntity();
                    prescriptionTypeRepository.save(entity);
                    totalSaved++;

                } catch (Exception e) {
                    totalSkipped++;
                    log.warn("[예외 발생] itemSeq={} 저장 실패 - {}", dto.getItemSeq(), e.getMessage());
                }
            }

            log.info("[{}페이지] 저장: {}건 / 스킵: {}건 / 누적 저장: {}", pageNo, totalSaved, totalSkipped, totalSaved);

            pageNo++;
        } while ((pageNo - 1) * MAX_ROW_CNT < totalCount);

        log.info("저장 완료 - 총 저장: {}, 총 스킵: {}", totalSaved, totalSkipped);

        if (totalSaved + totalSkipped != totalCount) {
            throw new IllegalStateException(String.format(
                    "처리된 건수 불일치: 저장 %d + 스킵 %d ≠ 전체 %d", totalSaved, totalSkipped, totalCount
            ));
        }
    }

    // 페이지별 수집 - PRESCRIPTION_TYPE
    public void fetchPage2(ApiType apiType, int pageNo) throws Exception {
        LocalDataResponse<PrescriptionTypeDto> response = fetch(apiType, pageNo);
        List<PrescriptionTypeDto> items = response.getBody().getItems();

        List<PrescriptionType> toSave = items.stream()
                .filter(dto -> {
                    boolean exists = prescriptionTypeRepository.existsByItemSeq(dto.getItemSeq());
                    if (exists) {
                        log.info("[중복] itemSeq={} 저장 스킵", dto.getItemSeq());
                        return false;
                    }
                    return true;
                })
                .map(PrescriptionTypeDto::toEntity)
                .collect(Collectors.toList());

        prescriptionTypeRepository.saveAll(toSave);
        log.info("[{}페이지] 저장 완료: {}건 / 스킵된 건수: {}", pageNo, toSave.size(), items.size() - toSave.size());
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
