package hexfive.ismedi.openApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexfive.ismedi.domain.DrugInfo;
import hexfive.ismedi.openApi.dto.DrugInfoDto;
import hexfive.ismedi.openApi.dto.LocalDataResponse;
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
    private final ObjectMapper objectMapper;

    @Value("${api.key}")
    private String serviceKey;

    // 전체 데이터 수집
    public void fetchAll() throws Exception {
        int pageNo = 1;
        int numOfRows = 100;
        int totalCount;
        int totalSaved = 0;

        do {
            LocalDataResponse response = fetchByPage(pageNo, numOfRows);
            totalCount = response.getBody().getTotalCount();
            List<DrugInfoDto> items = response.getBody().getItems();

            List<DrugInfo> entities = items.stream()
                    .map(DrugInfoDto::toEntity)
                    .collect(Collectors.toList());

            drugInfoRepository.saveAll(entities);
            totalSaved += entities.size();
            log.info("[{}페이지] 전체 저장 개수: {}", pageNo, totalSaved);

            pageNo++;
        } while ((pageNo - 1) * numOfRows < totalCount);

        if (totalSaved != totalCount)
            throw new IllegalStateException(String.format("저장된 건수 : %d / 전체 건수 : %d - 불일치", totalSaved, totalCount));
    }

    // 페이지별 수집
    public void fetchPage(int pageNo) throws Exception {
        LocalDataResponse response = fetchByPage(pageNo, 100);
        List<DrugInfoDto> items = response.getBody().getItems();

        List<DrugInfo> entities = items.stream()
                .map(DrugInfoDto::toEntity)
                .collect(Collectors.toList());

        drugInfoRepository.saveAll(entities);
        log.info("[{}페이지] 저장한 약 정보 수: {}", pageNo, entities.size());
    }

    // 공통 호출 로직
    private LocalDataResponse fetchByPage(int pageNo, int numOfRows) throws Exception {
        String apiUrl = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";
        String type = "json";

        String uriStr = String.format(
                "%s?serviceKey=%s&pageNo=%d&numOfRows=%d&type=%s",
                apiUrl, serviceKey, pageNo, numOfRows, type
        );

        URI uri = new URI(uriStr);
        log.info("uri\n"+ uri);

        RestTemplate template = new RestTemplate();
        String jsonResponse = template.getForObject(uri, String.class);

        return objectMapper.readValue(jsonResponse, LocalDataResponse.class);
    }
}
