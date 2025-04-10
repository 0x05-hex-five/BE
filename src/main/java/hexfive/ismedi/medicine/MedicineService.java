package hexfive.ismedi.medicine;

import hexfive.ismedi.domain.Medicine;
import hexfive.ismedi.openApi.ApiType;
import hexfive.ismedi.openApi.OpenApiService;
import hexfive.ismedi.openApi.dto.DrugInfoDto;
import hexfive.ismedi.openApi.dto.PrescriptionTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {
    private final OpenApiService openApiService;
    private final MedicineRepository medicineRepository;

    public void setMedicineData() throws Exception {
        List<Medicine> medicines = new ArrayList<>();
        List<DrugInfoDto> drugInfoList = openApiService.getDrugInfoDtoList(ApiType.DRUG_INFO);

        for (DrugInfoDto drugInfo : drugInfoList) {
            Medicine medicine = drugInfo.toMedicine();
            try {
                PrescriptionTypeDto prescTypeDto = openApiService.getPrescriptionTypeDtoByItemName(drugInfo.getItemName());
                prescTypeDto.applyTo(medicine);
            } catch (Exception e) {
                log.warn("분류 데이터 조회 실패: itemName = {}", drugInfo.getItemName());
            }
            medicines.add(medicine);
        }
        log.info("drugInfo 개수: {}개 / 저장된 약 개수: {}개", drugInfoList.size(), medicines.size());
        medicineRepository.saveAll(medicines);
    }
}
