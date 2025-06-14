package hexfive.ismedi.medicine;

import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.medicine.dto.DURInteractionDto;
import hexfive.ismedi.medicine.dto.ResInteractionDto;
import hexfive.ismedi.medicine.dto.ResMedicineDetailDto;
import hexfive.ismedi.medicine.dto.ResMedicineDto;
import hexfive.ismedi.openApi.APIType;
import hexfive.ismedi.openApi.OpenAPIService;
import hexfive.ismedi.openApi.data.ImageAndClass.ImageAndClass;
import hexfive.ismedi.openApi.data.ImageAndClass.ImageAndClassRepository;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfo;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionType;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfoRepository;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionTypeRepository;
import hexfive.ismedi.openApi.data.xml.XMLDrugInfoRepository;
import hexfive.ismedi.openApi.data.xml.XmlDrugInfo;
import hexfive.ismedi.openApi.dto.OpenAPIBody;
import hexfive.ismedi.openApi.dto.OpenAPIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static hexfive.ismedi.global.exception.ErrorCode.MEDICINE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {

    private final XMLDrugInfoRepository xmlDrugInfoRepository;
    private final ImageAndClassRepository imageAndClassRepository;
    private final MedicineRepository medicineRepository;
    private final OpenAPIService openAPIService;
    private final Set<String> requiredItemSeqs = new HashSet<>(Set.of(
            // AI
            "197000040", "198100119", "198600312", "198900463", "199200588", "200000801", "199702182", "200000796", "200000797", "198500321", "199400492", "200300401", "200410085", "200410086", "200410090", "200511904", "200605262", "200605263", "200610660", "200500287", "200500288", "200101678", "200511057", "200511058", "200511059", "200210097", "200410326", "200511256", "200302297", "200403500", "200600026", "200606210", "200700907",
            // DUR
            "200500257", "201901612"
    ));

    public void initMedicineTable() {
        List<XmlDrugInfo> drugInfos = xmlDrugInfoRepository.findAll();

        for (XmlDrugInfo drugInfo : drugInfos) {
            if (requiredItemSeqs.isEmpty())
                break;

            String itemSeq = drugInfo.getItemSeq();
            Optional<ImageAndClass> imageAndClass = imageAndClassRepository.findById(itemSeq);

            if (requiredItemSeqs.contains(itemSeq)) {
                log.info("{} : 제거,  남은 itemSeq : {} ", itemSeq, requiredItemSeqs.size());
                requiredItemSeqs.remove(itemSeq);
            } else if (imageAndClass.isEmpty()) {
                continue;
            }

            try {
                Medicine medicine = Medicine.builder()
                        .itemSeq(itemSeq)
                        .entpName(drugInfo.getEntpName())
                        .itemName(drugInfo.getItemName())
                        .etcOtcCode(drugInfo.getEtcOtcCode())
                        .chart(drugInfo.getChart())
                        .materialName(drugInfo.getMaterialName())
                        .eeDocText(drugInfo.getEeDocText())
                        .udDocText(drugInfo.getUdDocText())
                        .nbDocText(drugInfo.getNbDocText())
                        .classNoName(imageAndClass.map(ImageAndClass::getClassName).orElse(null))
                        .itemImage(imageAndClass.map(ImageAndClass::getItemImage).orElse(null))
                        .build();

                medicineRepository.save(medicine);
            } catch (Exception e) {
                log.warn("itemSeq={} 약 데이터 저장 실패 : {}", drugInfo.getItemSeq(), e.getMessage());
            }
        }
    }

    public List<ResMedicineDto> getMedicines(String name, MedicineType type) {
        List<Medicine> medicines;
        if (type.isAll() && name.isBlank()) {
            medicines = medicineRepository.findAll();
        } else if (type.isAll()) {
            medicines = medicineRepository.findAllByItemNameContaining(name);
        } else if (name.isBlank()) {
            medicines = medicineRepository.findAllByEtcOtcCode(type.getValue());
        } else {
            medicines = medicineRepository.findAllByItemNameContainingAndEtcOtcCode(name, type.getValue());
        }

        return medicines.stream()
                .map(ResMedicineDto::fromEntity)
                .toList();
    }

    public ResMedicineDetailDto getMedicine(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약 id 입니다."));
        return ResMedicineDetailDto.fromEntity(medicine);
    }

    public ResInteractionDto checkInteraction(Long medicineId1, Long medicineId2) throws Exception {
        Medicine medicine1 = medicineRepository.findById(medicineId1)
                .orElseThrow(() -> new CustomException(MEDICINE_NOT_FOUND, medicineId1));
        Medicine medicine2 = medicineRepository.findById(medicineId2)
                .orElseThrow(() -> new CustomException(MEDICINE_NOT_FOUND, medicineId2));

        Map<String, String> params = new HashMap<>();
        params.put("itemSeq", medicine1.getItemSeq());

        // medicine1의 병용 금지 약 목록 조회
        int pageNo = 1;
        int totalCount = 0;
        int curCount = 0;
        List<DURInteractionDto> interactionsItems = new ArrayList<>();
        do {
            OpenAPIResponse<DURInteractionDto> response = openAPIService.fetch(APIType.DUR_INTERACTION, pageNo, params);
            List<DURInteractionDto> items = response.getBody().getItems();

            if (totalCount == 0) {
                totalCount = response.getBody().getTotalCount();
            }

            interactionsItems.addAll(items);
            curCount += items.size();
            pageNo++;
        } while (curCount < totalCount);

        for (DURInteractionDto interactionItem : interactionsItems) {
            log.info("{} {}", interactionItem.getMixtureItemSeq(), interactionItem.getItemName());
            // 동시 복용 불가능
            if (interactionItem.getMixtureItemSeq().equals(medicine2.getItemSeq())) {
                return ResInteractionDto.builder()
                        .itemName1(medicine1.getItemName())
                        .itemName2(medicine2.getItemName())
                        .isProhibit(true)
                        .prohibitContent(interactionItem.getProhbtContent())
                        .build();
            }
        }
        // 동시 복용 가능
        return ResInteractionDto.builder()
                .itemName1(medicine1.getItemName())
                .itemName2(medicine2.getItemName())
                .isProhibit(false)
                .prohibitContent(null)
                .build();
    }
}
