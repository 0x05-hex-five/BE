package hexfive.ismedi.openApi;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexfive.ismedi.openApi.dto.DrugInfoDto;
import hexfive.ismedi.openApi.dto.OpenApiResponse;
import hexfive.ismedi.openApi.dto.PrescriptionTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiService {
    private final ObjectMapper objectMapper;

    @Value("${api.key}")
    private String serviceKey;
    private int MAX_ROW_CNT = 100;

    // DRUG_INFO 전체 데이터 반환
    public List<DrugInfoDto> getDrugInfoDtoList(ApiType apiType) throws Exception {
        List<DrugInfoDto> drugInfoDtoList = new ArrayList<>();
        int pageNo = 1;
        int totalCount;
        int totalSaved = 0;

        do {
            Map<String, String> params = new HashMap<>();
            params.put("pageNo", String.valueOf(pageNo));
            params.put("numOfRows", String.valueOf(MAX_ROW_CNT));

            OpenApiResponse<DrugInfoDto> response = fetch(apiType, params);
            totalCount = response.getBody().getTotalCount();
            List<DrugInfoDto> items = response.getBody().getItems();

            drugInfoDtoList.addAll(items);
            totalSaved += items.size();
            log.info("[{}페이지] 전체 저장 개수: {}", pageNo, totalSaved);

            pageNo++;
        } while ((pageNo - 1) * MAX_ROW_CNT < totalCount);

        if (totalSaved != totalCount) {
            throw new IllegalStateException(String.format("저장된 건수 : %d / 전체 건수 : %d - 불일치", totalSaved, totalCount));
        }
        return drugInfoDtoList;
    }

    // ITEM_NAME에 따른 PRESCRIPTION_TYPE 데이터 반환
    public PrescriptionTypeDto getPrescriptionTypeDtoByItemName(String itemName) throws Exception {
        // 특수 기호, 숫자 제외 (인코딩 오류 방지)
        String searchName = itemName.split("[^\\p{IsHangul}\\p{IsAlphabetic}]")[0];

        Map<String, String> params = new HashMap<>();
        params.put("ITEM_NAME", searchName);

        OpenApiResponse<PrescriptionTypeDto> response = fetch(ApiType.PRESCRIPTION_TYPE, params);
        List<PrescriptionTypeDto> items = response.getBody().getItems();

        if (items.size() != 1) {
            List<String> itemNames = items.stream()
                    .map(PrescriptionTypeDto::getItemName)
                    .collect(Collectors.toList());
            log.warn("[itemName {}] 약품명 목록: {}", itemName, itemNames);
        }

        // 제품명이 완전 일치하는 항목
        Optional<PrescriptionTypeDto> exactMatch = items.stream()
                .filter(item -> itemName.equals(item.getItemName()))
                .findFirst();

        // 일치하는 항목이 없으면 제품명이 짧은 것 먼저 매칭
        return exactMatch.orElseGet(() ->
                items.stream()
                        .min(Comparator.comparingInt(item -> item.getItemName().length()))
                        .orElse(null)
        );
    }

    // 공통 호출 로직
    private <T> OpenApiResponse<T> fetch(ApiType apiType, Map<String, String> queryParams) throws Exception {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(apiType.getUrl());
        uriBuilder.append("?serviceKey=").append(serviceKey);
        uriBuilder.append("&type=json");

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
                uriBuilder.append("&")
                        .append(entry.getKey())
                        .append("=")
                        .append(encodedValue);
            }
        }

        URI uri = new URI(uriBuilder.toString());
        log.info("uri: {}", uri.toString());

        RestTemplate template = new RestTemplate();
        String jsonResponse = template.getForObject(uri, String.class);

        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(OpenApiResponse.class, apiType.getDtoClass());

        return objectMapper.readValue(jsonResponse, javaType);
    }
}
