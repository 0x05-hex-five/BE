package hexfive.ismedi.fastApi.dto;

import hexfive.ismedi.medicine.dto.ResMedicineDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResAiMedicineDto {
    private ResMedicineDetailDto medicine;
    private double confidence;

    public static ResAiMedicineDto of(ResMedicineDetailDto detailDto, Double confidence) {
        return ResAiMedicineDto.builder()
                .medicine(detailDto)
                .confidence(confidence)
                .build();
    }
}
