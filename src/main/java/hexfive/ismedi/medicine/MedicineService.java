package hexfive.ismedi.medicine;

import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.global.exception.ErrorCode;
import hexfive.ismedi.medicine.dto.ResMedicineDetailDto;
import hexfive.ismedi.medicine.dto.ResMedicineDto;
import hexfive.ismedi.openApi.APIType;
import hexfive.ismedi.openApi.OpenAPIService;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfo;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionType;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfoRepository;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionTypeRepository;
import hexfive.ismedi.openApi.dto.OpenAPIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;

import static hexfive.ismedi.global.exception.ErrorCode.MEDICINE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {

    private final PrescriptionTypeRepository prescriptionTypeRepository;
    private final DrugInfoRepository drugInfoRepository;
    private final MedicineRepository medicineRepository;
    private final OpenAPIService openAPIService;
    List<Long> checkList = new ArrayList<>();

    public void mergeToMedicineTable() {
        List<PrescriptionType> prescriptionTypes = prescriptionTypeRepository.findAll();

        for (PrescriptionType pt : prescriptionTypes) {
            try {
                String itemSeq = pt.getItemSeq();
                DrugInfo di = drugInfoRepository.findByItemSeq(itemSeq).orElse(null);

                Medicine medicine = Medicine.builder()
                        .itemSeq(itemSeq)
                        .entpName(pt.getEntpName())
                        .itemName(pt.getItemName())
                        .etcOtcCodeName(pt.getEtcOtcCodeName())
                        .classNoName(pt.getClassNoName())
                        .itemImage(di != null ? di.getItemImage() : null)
                        .efcyQesitm(di != null ? di.getEfcyQesitm() : null)
                        .useMethodQesitm(di != null ? di.getUseMethodQesitm() : null)
                        .atpnQesitm(di != null ? di.getAtpnQesitm() : null)
                        .intrcQesitm(di != null ? di.getIntrcQesitm() : null)
                        .seQesitm(di != null ? di.getSeQesitm() : null)
                        .depositMethodQesitm(di != null ? di.getDepositMethodQesitm() : null)
                        .build();

                medicineRepository.save(medicine);
            } catch (Exception e) {
                log.warn("itemSeq={} 약 데이터 저장 실패 : {}", pt.getItemSeq(), e.getMessage());
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
            medicines = medicineRepository.findAllByEtcOtcCodeName(type.getValue());
        } else {
            medicines = medicineRepository.findAllByItemNameContainingAndEtcOtcCodeName(name, type.getValue());
        }

        for (Medicine medicine: medicines) {
            log.info("{}", medicine);
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
