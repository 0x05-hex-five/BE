package hexfive.ismedi.medicine;

import hexfive.ismedi.openApi.data.drugInfo.DrugInfo;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionType;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfoRepository;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionTypeRepository;
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
        List<DrugInfo> drugInfos = drugInfoRepository.findAll();

        for (DrugInfo di : drugInfos) {
            String itemSeq = di.getItemSeq();
            Optional<PrescriptionType> optionalPresc = prescriptionTypeRepository.findByItemSeq(itemSeq);

            optionalPresc.ifPresent(pt -> {
                MedicineController.Medicine medicine = MedicineController.Medicine.builder()
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
}
