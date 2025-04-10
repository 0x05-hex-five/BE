package hexfive.ismedi.medicine;

import hexfive.ismedi.domain.DrugInfo;
import hexfive.ismedi.domain.Medicine;
import hexfive.ismedi.domain.PrescriptionType;
import hexfive.ismedi.openApi.DrugInfoRepository;
import hexfive.ismedi.openApi.PrescriptionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final PrescriptionTypeRepository prescriptionTypeRepository;
    private final DrugInfoRepository drugInfoRepository;
    private final MedicineRepository medicineRepository;

    public void mergeToMedicineTable() {
        List<PrescriptionType> prescriptions = prescriptionTypeRepository.findAll();

        for (PrescriptionType pt : prescriptions) {
            String itemSeq = pt.getItemSeq();
            Optional<DrugInfo> optionalDrugInfo = drugInfoRepository.findByItemSeq(itemSeq);

            optionalDrugInfo.ifPresent(drugInfo -> {
                Medicine medicine = Medicine.builder()
                        .itemSeq(itemSeq)
                        .entpName(pt.getEntpName())
                        .itemName(pt.getItemName())
                        .etcOtcCodeName(pt.getEtcOtcCodeName())
                        .classNoName(pt.getClassNoName())

                        .efcyQesitm(drugInfo.getEfcyQesitm())
                        .useMethodQesitm(drugInfo.getUseMethodQesitm())
                        .atpnQesitm(drugInfo.getAtpnQesitm())
                        .intrcQesitm(drugInfo.getIntrcQesitm())
                        .seQesitm(drugInfo.getSeQesitm())
                        .depositMethodQesitm(drugInfo.getDepositMethodQesitm())
                        .build();

                medicineRepository.save(medicine);
            });
        }
    }
}
