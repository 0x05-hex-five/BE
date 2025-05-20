package hexfive.ismedi.openApi;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.medicine.Medicine;
import hexfive.ismedi.medicine.MedicineType;
import hexfive.ismedi.medicine.dto.ResMedicineDto;
import hexfive.ismedi.openApi.data.ImageAndClass.ImageAndClass;
import hexfive.ismedi.openApi.data.ImageAndClass.ImageAndClassRepository;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfo;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionType;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfoRepository;
import hexfive.ismedi.openApi.data.xml.XMLDrugInfoRepository;
import hexfive.ismedi.openApi.data.xml.XmlDrugInfo;
import hexfive.ismedi.openApi.data.xml.dto.DrugItem;
import hexfive.ismedi.openApi.data.xml.dto.XMLAPIResponse;
import hexfive.ismedi.openApi.dto.OpenAPIResponse;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionTypeRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
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
    private final XMLDrugInfoRepository xmlDrugInfoRepository;
    private final ImageAndClassRepository imageAndClassRepository;
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
                case IMAGE_AND_CLASS -> fetchImageAndClassPage(apiType, pageNo);
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

    // 페이지별 수집 - IMAGE_AND_CLASS
    public PageResult fetchImageAndClassPage(APIType apiType, int pageNo) throws Exception {
        OpenAPIResponse<ImageAndClass> response = fetch(apiType, pageNo, null);
        List<ImageAndClass> items = response.getBody().getItems();

        List<ImageAndClass> toSave = items.stream()
                .filter(item -> !imageAndClassRepository.existsByItemSeq(item.getItemSeq()))
                .collect(Collectors.toList());

        imageAndClassRepository.saveAll(toSave);
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

    public void fetchXMLAll(APIType apiType) {
        int pageNo = 1;
        int totalCount;
        int totalSaved = 0;
        int totalSkipped = 0;

        do {
            PageResult result = switch (apiType) {
                case XML -> fetchXMLPage(apiType, pageNo);
                default -> throw new IllegalStateException("지원하지 않는 API type입니다: " + apiType);
            };

            totalCount = result.totalCount();
            totalSaved += result.saved();
            totalSkipped += result.skipped();

            pageNo++;
        } while ((pageNo - 1) * MAX_ROW_CNT < totalCount); // MAX_ROW_CNT는 페이지당 최대 개수

        log.info("XML 저장 완료 - 총 저장: {}, 총 스킵: {}", totalSaved, totalSkipped);

        if (totalSaved + totalSkipped != totalCount) {
            throw new CustomException(MISMATCH_COUNT, totalSaved, totalSkipped, totalCount);
        }
    }

    public PageResult fetchXMLPage(APIType apiType, int pageNo) {
        XMLAPIResponse response = fetchXML(apiType, pageNo, new HashMap<>());
        List<DrugItem> items = response.getBody().getItems();

        if (items == null || items.isEmpty()) {
            return new PageResult(0, 0, response.getBody().getTotalCount());
        }

        List<XmlDrugInfo> xmlDrugInfos = items.stream()
                .map(XmlDrugInfo::from)
                .toList();

        List<XmlDrugInfo> toSave = xmlDrugInfos.stream()
                .filter(item -> !xmlDrugInfoRepository.existsByItemSeq(item.getItemSeq()))
                .toList();

        try {
            xmlDrugInfoRepository.saveAll(toSave);

        } catch (Exception e) {
            log.warn("데이터 저장 실패 [page: {}] - {}", pageNo, e.getMessage());
        }
        log.info("[XML {}페이지] 저장: {} / 스킵: {}", pageNo, toSave.size(), xmlDrugInfos.size() - toSave.size());

        return new PageResult(toSave.size(), xmlDrugInfos.size() - toSave.size(), response.getBody().getTotalCount());
    }

    public XMLAPIResponse fetchXML(APIType apiType, int pageNo, Map<String, String> params) {
        String apiUrl = apiType.getUrl();
        String type = "xml";  // XML을 요청한다고 명시
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

        try {
            URI uri = new URI(uriBuilder.toString());
            log.info("uri: {}", uri);

            // XML을 문자열로 받기
            RestTemplate template = new RestTemplate();
            String xmlResponse = template.getForObject(uri, String.class);

            // JAXBContext를 이용한 XML 파싱
            JAXBContext jaxbContext = JAXBContext.newInstance(XMLAPIResponse.class, apiType.getEntity());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // XML 문자열 -> OpenAPIResponse 객체
            StringReader reader = new StringReader(xmlResponse);
            @SuppressWarnings("unchecked")
            XMLAPIResponse result = (XMLAPIResponse) unmarshaller.unmarshal(reader);
            return result;
        } catch (Exception e) {
            log.warn("API 호출 실패 : {}", pageNo);
        }
        return null;
    }


    public List<XmlDrugInfo> getNewMedicines(String name, MedicineType type) {
        List<XmlDrugInfo> medicines;
        if (type.isAll() && name.isBlank()) {
            medicines = xmlDrugInfoRepository.findAll();
        } else if (type.isAll()) {
            medicines = xmlDrugInfoRepository.findAllByItemNameContaining(name);
        } else if (name.isBlank()) {
            medicines = xmlDrugInfoRepository.findAllByEtcOtcCode(type.getValue());
        } else {
            medicines = xmlDrugInfoRepository.findAllByItemNameContainingAndEtcOtcCode(name, type.getValue());
        }

        return medicines;
    }
}
