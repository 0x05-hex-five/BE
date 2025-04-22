package hexfive.ismedi.medicine;

import hexfive.ismedi.medicine.dto.ResMedicineDetailDto;
import hexfive.ismedi.medicine.dto.ResMedicineDto;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfo;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionType;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfoRepository;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {

    private final PrescriptionTypeRepository prescriptionTypeRepository;
    private final DrugInfoRepository drugInfoRepository;
    private final MedicineRepository medicineRepository;

    public void mergeToMedicineTable() {
        List<DrugInfo> drugInfos = drugInfoRepository.findAll();

        for (DrugInfo di : drugInfos) {
            String itemSeq = di.getItemSeq();
            Optional<PrescriptionType> optionalPresc = prescriptionTypeRepository.findByItemSeq(itemSeq);

            optionalPresc.ifPresent(pt -> {
                Medicine medicine = Medicine.builder()
                        .itemSeq(itemSeq)
                        .entpName(pt.getEntpName())
                        .itemName(pt.getItemName())
                        .etcOtcCodeName(pt.getEtcOtcCodeName())
                        .classNoName(pt.getClassNoName())

                        .efcyQesitm(di.getEfcyQesitm())
                        .useMethodQesitm(di.getUseMethodQesitm())
                        .atpnQesitm(di.getAtpnQesitm())
                        .intrcQesitm(di.getIntrcQesitm())
                        .seQesitm(di.getSeQesitm())
                        .depositMethodQesitm(di.getDepositMethodQesitm())
                        .build();

                medicineRepository.save(medicine);
            });
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
}
