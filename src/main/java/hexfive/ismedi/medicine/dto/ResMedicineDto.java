package hexfive.ismedi.medicine.dto;

import hexfive.ismedi.medicine.Medicine;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResMedicineDto {
    private Long id;
    private String name; // 약 이름
    private String image; // 이미지

    private String type; // 의약품 분류
    private String className; // 약 분류

    public static ResMedicineDto fromEntity(Medicine medicine) {
        return ResMedicineDto.builder()
                .id(medicine.getId())
                .name(medicine.getItemName())
                .image(medicine.getItemImage())
                .type(medicine.getEtcOtcCode())
                .className(medicine.getClassNoName())
                .build();
    }
}
