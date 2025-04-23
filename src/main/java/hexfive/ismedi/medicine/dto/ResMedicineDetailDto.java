package hexfive.ismedi.medicine.dto;

import hexfive.ismedi.medicine.Medicine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResMedicineDetailDto {
    private Long id;
    private String name;            // 약 이름
    private String image;           // 이미지
    private String type;            // 의약품 분류(전문/일반)
    private String className;       // 약 정보

    private String efficacy;        // 효능
    private String useMethod;       // 사용법
    private String storageMethod;   // 보관법
    private String precaution;      // 주의사항
    private String interaction;     // 상호작용
    private String sideEffect;      // 부작용

    public static ResMedicineDetailDto fromEntity(Medicine medicine) {
        return ResMedicineDetailDto.builder()
                .id(medicine.getId())
                .name(medicine.getItemName())
                .image(medicine.getItemImage())
                .type(medicine.getEtcOtcCodeName())
                .className(medicine.getClassNoName())
                .efficacy(medicine.getEfcyQesitm())
                .useMethod(medicine.getUseMethodQesitm())
                .storageMethod(medicine.getDepositMethodQesitm())
                .precaution(medicine.getAtpnQesitm())
                .interaction(medicine.getIntrcQesitm())
                .sideEffect(medicine.getSeQesitm())
                .build();
    }
}
