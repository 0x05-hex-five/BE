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

    private String efficacy;        // 효능효과
    private String useMethod;       // 용법용량
    private String precaution;      // 주의사항

    public static ResMedicineDetailDto fromEntity(Medicine medicine) {
        return ResMedicineDetailDto.builder()
                .id(medicine.getId())
                .name(medicine.getItemName())
                .image(medicine.getItemImage())
                .type(medicine.getEtcOtcCode())
                .className(medicine.getClassNoName())
                .efficacy(medicine.getEeDocText())
                .useMethod(medicine.getUdDocText())
                .precaution(medicine.getNbDocText())
                .build();
    }
}
